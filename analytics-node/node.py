import os
import uvicorn
from fastapi import FastAPI, Request, Query, HTTPException, Depends
from fastapi.middleware.cors import CORSMiddleware
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from dotenv import load_dotenv
from supabase import create_client, Client
import pandas as pd
import joblib 
import shap
import numpy as np
import requests
import json

load_dotenv()

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Obtenemos los parámetros del fichero .env
SUPABASE_URL = os.getenv("SUPABASE_URL")
SUPABASE_KEY = os.getenv("SUPABASE_KEY")
TOMTOM_KEY = os.getenv("TOMTOM_API_KEY")
supabase: Client = create_client(SUPABASE_URL, SUPABASE_KEY)

# Carga del modelo rf de anomalias
MODEL_ANOMALIES_PATH = "models/random_forest_anomalies.pkl"
rf_model = joblib.load(MODEL_ANOMALIES_PATH)
shap_explainer = shap.TreeExplainer(rf_model)

# Carga del modelo LightGBM para ocupación
MODEL_OCCUPANCY_PATH = "models/lightgbm_occupancy.pkl"
if os.path.exists(MODEL_OCCUPANCY_PATH):
    lgb_model = joblib.load(MODEL_OCCUPANCY_PATH)
else:
    lgb_model = None

# Features para la detección de anomalías
FEATURE_COLS = [
    'account_age_days',
    'reservation_count',
    'cancellation_ratio',
    'expired_ratio',
    'avg_overstay_minutes',
    'total_overstays',
    'avg_hours_between_reservations',
    'booking_hour',
    'booking_weekday',
]

# Features para la predicción de ocupación
FEATURE_OCCUPANCY_COLS = [
    'current_occupancy_ratio',
    'traffic_congestion_index',
    'booking_hour',
    'booking_weekday',
    'capacity'
]

# Definimos un mapeo entre las features y el tipo de alerta
FEATURE_TO_ALERT_TYPE = {
    'expired_ratio':                    'RECURRENT_EXPIRED',
    'cancellation_ratio':               'RECURRENT_CANCELLATIONS',
    'avg_overstay_minutes':             'RECURRENT_OVERSTAY',
    'total_overstays':                  'RECURRENT_OVERSTAY',
    'account_age_days':                 'SUSPICIOUS_NEW_ACCOUNT',
    'avg_hours_between_reservations':   'UNUSUAL_BOOKING_FREQUENCY',
    'booking_hour':                     'UNUSUAL_BOOKING_HOUR',
    'booking_weekday':                  'UNUSUAL_BOOKING_WEEKDAY',
    'reservation_count':                'ABNORMAL_BOOKING_PATTERN',
}


def get_user_history(account_id: str):
    """
    Obtiene el historial reciente de reservas y excesos de tiempo de un usuario
    junto con la fecha de creación de la cuenta
    """

    acc_query = supabase.table("accounts") \
        .select("created_at") \
        .eq("account_id", account_id) \
        .single() \
        .execute()
    
    account_created_at = acc_query.data.get("created_at") if acc_query.data else None

    res_query = supabase.table("reservations") \
        .select("reservation_id, in_time, out_time, state, created_at") \
        .eq("account_id", account_id) \
        .order("created_at", desc=True) \
        .limit(20) \
        .execute()
    
    reservations = res_query.data
    
    if not reservations:
        return pd.DataFrame(), pd.DataFrame(), account_created_at

    res_ids = [r["reservation_id"] for r in reservations]
    over_query = supabase.table("overstays") \
        .select("reservation_id, extra_minutes") \
        .in_("reservation_id", res_ids) \
        .execute()

    df_reservations = pd.DataFrame(reservations)
    df_overstays = pd.DataFrame(over_query.data)
    
    return df_reservations, df_overstays, account_created_at


def get_parking_owner_id(parking_area_id: str) -> str:
    """
    Obtiene el identificador del propietario de un área de parking.
    """

    query = supabase.table("parkingareas") \
        .select("owner_id") \
        .eq("parking_area_id", parking_area_id) \
        .single() \
        .execute()
    
    if query.data:
        return query.data.get("owner_id")
    raise ValueError(f"Parking not found with ID: {parking_area_id}")


def build_feature_vector(new_reservation, df_reservations, df_overstays, account_created_at):
    """
    Construye el vector de características utilizado por el modelo de detección
    de anomalías.
    """

    features = {}

    if account_created_at:
        created_dt = pd.to_datetime(account_created_at, utc=True)
        features['account_age_days'] = (pd.Timestamp.now(tz='UTC') - created_dt).days
    else:
        features['account_age_days'] = 0

    if df_reservations.empty:
        features['reservation_count']                = 0
        features['cancellation_ratio']               = 0.0
        features['expired_ratio']                    = 0.0
        features['avg_overstay_minutes']             = 0.0
        features['total_overstays']                  = 0
        features['avg_hours_between_reservations']   = 0.0
    else:
        n = len(df_reservations)
        features['reservation_count']  = n
        features['cancellation_ratio'] = len(df_reservations[df_reservations['state'] == 'CANCELLED']) / n
        features['expired_ratio']      = len(df_reservations[df_reservations['state'] == 'EXPIRED']) / n

        if not df_overstays.empty:
            features['avg_overstay_minutes'] = df_overstays['extra_minutes'].mean()
            features['total_overstays']      = len(df_overstays)
        else:
            features['avg_overstay_minutes'] = 0.0
            features['total_overstays']      = 0

        if n > 1:
            timestamps = pd.to_datetime(df_reservations['created_at'], utc=True).sort_values()
            diffs_hours = timestamps.diff().dropna().dt.total_seconds() / 3600
            features['avg_hours_between_reservations'] = diffs_hours.mean()
        else:
            features['avg_hours_between_reservations'] = 0.0

    in_time_dt = pd.to_datetime(new_reservation.get("in_time"), utc=True)
    features['booking_hour']    = in_time_dt.hour
    features['booking_weekday'] = in_time_dt.dayofweek

    return features


def get_alert_type(df_input: pd.DataFrame) -> tuple[str, float]:
    """
    Determina el tipo de alerta asociado a una anomalía detectada.
    """
    try:
        shap_results = shap_explainer.shap_values(df_input)

        # Normalizar a un array de numpy
        if hasattr(shap_results, "values"):
            vals = np.array(shap_results.values)
        elif isinstance(shap_results, list):
            # Para clasificadores, suele ser [clase_0, clase_1]
            vals = np.array(shap_results[1] if len(shap_results) > 1 else shap_results[0])
        else:
            vals = np.array(shap_results)

        # Si el array es tiene 3 dimensiones, nos quedamos con la clase 1 (anomalía)
        if vals.ndim == 3:
            vals = vals[:, :, 1] if vals.shape[2] > 1 else vals[:, :, 0]

        if vals.ndim == 2:
            row_vals = vals[0]
        else:
            row_vals = vals.flatten()

        # Convertimos a float puro de Python para evitar errores con tipos numpy en diccionarios o comparaciones
        row_vals = [float(x) for x in row_vals]

        # Calculamos el score de anomalía (suma de contribuciones positivas)
        anomaly_score = sum([x for x in row_vals if x > 0])

        # Mapeamos cada feature a su contribución
        contributions = {}
        for i, col in enumerate(FEATURE_COLS):
            if i < len(row_vals):
                contributions[col] = row_vals[i]
            else:
                contributions[col] = 0.0

        # Obtenemos la feature que más ha contribuido a la decisión
        top_feature = max(contributions, key=contributions.get)

        alert_type = FEATURE_TO_ALERT_TYPE.get(top_feature, 'ABNORMAL_BOOKING_PATTERN')
        return alert_type, anomaly_score

    except Exception as e:
        print(f"Error inside get_alert_type: {e}")
        # Fallback por seguridad
        return 'ABNORMAL_BOOKING_PATTERN', 0.0

@app.post("/new-reservation")
async def handle_new_reservation(request: Request):
    """
    Procesa una nueva reserva y evalúa si representa un comportamiento anómalo.
    """

    try:
        payload = await request.json()
        new_reservation = payload.get("record", {})
        
        account_id      = new_reservation.get("account_id")
        reservation_id  = new_reservation.get("reservation_id")
        parking_area_id = new_reservation.get("parking_area_id")
        
        if not account_id or not parking_area_id:
            return {"status": "ignored", "message": "Missing required reservation parameters"}

        print(f"\n Processing reservation {reservation_id} for user {account_id} ")

        # Obtenemos la información del usuario
        df_reservations, df_overstays, account_created_at = get_user_history(account_id)
        
        # Elaboramos la entrada para el modelo
        features = build_feature_vector(new_reservation, df_reservations, df_overstays, account_created_at)
        print(f"Feature vector: {features}")

        df_input = pd.DataFrame([features])
        prediction = rf_model.predict(df_input)

        # Convertir a booleano simple de Python de forma robusta
        is_anomaly = bool(np.asarray(prediction).flatten()[0])

        if is_anomaly:
            owner_id = get_parking_owner_id(parking_area_id)
            alert_type, anomaly_score = get_alert_type(df_input)
            print("Anomaly detected")
            supabase.table("alerts").insert({
                "account_id":      owner_id,
                "parking_area_id": parking_area_id,
                "reservation_id":  reservation_id,
                "alert_type":      alert_type,
                "alert_value":     anomaly_score
            }).execute()
            
        else:
            print("Normal booking pattern.")

        try:
            latitude, longitude, capacity, occupancy, threshold = get_parking_details(parking_area_id)

            if threshold is not None:
                # El umbral configurado por el usuario (0-100)
                t_val = float(np.asarray(threshold).flatten()[0]) if hasattr(threshold, "__iter__") else float(threshold)
                owner_id = get_parking_owner_id(parking_area_id)

                current_occupancy_pct = (occupancy / capacity) * 100 if capacity > 0 else 0

                print(f"Checking threshold for parking {parking_area_id}: Current {current_occupancy_pct:.1f}% vs Threshold {t_val}%")

                if current_occupancy_pct >= t_val:
                    print(f"Occupancy limit reached ({current_occupancy_pct:.2f}% >= {t_val:.2f}%). Sending alert.")
                    supabase.table("alerts").insert({
                        "account_id":      owner_id,
                        "parking_area_id": parking_area_id,
                        "alert_type":      "OCCUPANCY_LIMIT",
                        "alert_value":     float(current_occupancy_pct)
                    }).execute()
                else:
                    # Si no se ha superado el umbral con esta reserva, comprobamos la predicción
                    predicted_val, prediction_ratio, _, _ = _perform_occupancy_prediction(parking_area_id, latitude, longitude, capacity, occupancy)
                    prediction_pct = prediction_ratio * 100

                    print(f"Real occupancy below threshold. Checking prediction: {prediction_pct:.1f}% vs Threshold {t_val}%")

                    if prediction_pct >= t_val:
                        print(f"Predicted occupancy ({prediction_pct:.2f}%) exceeds threshold ({t_val:.2f}%). Sending alert.")
                        supabase.table("alerts").insert({
                            "account_id":      owner_id,
                            "parking_area_id": parking_area_id,
                            "alert_type":      "PREDICTED_OCCUPANCY",
                            "alert_value":     float(prediction_pct)
                        }).execute()
        except Exception as occ_err:
            print(f"Error checking occupancy/prediction: {occ_err}")

        # Se devuelve una respuesta satisfactoria a Supabase para que no reintente la petición
        return {
            "status": "success",
            "processed_reservation": reservation_id,
            "anomaly_detected": is_anomaly
        }
        
    except Exception as e:
        print(f"Error processing anomaly analysis: {e}")
        return {"status": "error", "message": str(e)}


def get_parking_details(parking_id: str) -> tuple[float, float, int, int, int]:
    """
    Obtiene la latitud, longitud, capacidad, ocupación actual y umbral de un parking desde Supabase.
    """
    query = supabase.table("parkingareas") \
        .select("capacity, current_occupancy, parking_area_location, occupancy_threshold") \
        .eq("parking_area_id", parking_id) \
        .single() \
        .execute()
    
    data = query.data
    if not data:
        raise ValueError(f"Parking not found: {parking_id}")
        
    capacity = data.get("capacity")
    occupancy = data.get("current_occupancy", 0)
    location = data.get("parking_area_location")
    threshold = data.get("occupancy_threshold")

    if not location:
        raise ValueError(f"Parking {parking_id} has no location data.")

    # Por si se devuelve como un diccionario directo o como un string JSON
    if isinstance(location, str):
        try:
            location = json.loads(location)
        except json.JSONDecodeError:
            # Si sigue viniendo el Hexadecimal EWKB crudo
            # aplicamos otra lógica: decodificar los bytes flotantes manualmente.
            try:
                import struct
                # El estándar EWKB de PostGIS guarda Longitud y Latitud en los últimos 16 bytes
                binary_data = bytes.fromhex(location)
                # Extraemos los dos double del final del paquete EWKB
                longitude, latitude = struct.unpack('<dd', binary_data[-16:])
                return latitude, longitude, capacity, occupancy, threshold
            except Exception as binary_err:
                raise ValueError(f"Error decoding geographic EWKB binary: {binary_err}")

    if isinstance(location, dict) and "coordinates" in location:
        coordinates = location.get("coordinates", [])
        if len(coordinates) == 2:
            longitude = coordinates[0]
            latitude = coordinates[1]
            return latitude, longitude, capacity, occupancy, threshold

    raise ValueError(f"Could not parse location format: {location}")

def _perform_occupancy_prediction(parking_id, latitude, longitude, capacity, occupancy):
    """
    Lógica interna compartida para ejecutar la inferencia del modelo LightGBM.
    """
    traffic_index = get_congestion_index(latitude, longitude)
    current_ratio = occupancy / capacity if capacity > 0 else 0.0

    # Extraer características de tiempo basadas en la zona horaria del servidor
    current_time = pd.Timestamp.now(tz='UTC')
    booking_hour = current_time.hour
    booking_weekday = current_time.dayofweek

    # Construcción del vector estructurado para el modelo
    features_dict = {
        'current_occupancy_ratio': current_ratio,
        'traffic_congestion_index': traffic_index,
        'booking_hour': booking_hour,
        'booking_weekday': booking_weekday,
        'capacity': capacity
    }

    df_input = pd.DataFrame([features_dict], columns=FEATURE_OCCUPANCY_COLS)

    if lgb_model is not None:
        prediction_ratio = float(lgb_model.predict(df_input)[0])
        predicted_val = int(round(prediction_ratio * capacity))

        base_confidence = 0.95
        if traffic_index == 0.0: base_confidence -= 0.15
        if current_ratio > 0.95 or current_ratio < 0.05: base_confidence -= 0.10
        confidence_score = max(0.50, base_confidence)
    else:
        # Fallback inteligente
        prediction_offset = 1 if traffic_index > 0.5 else 0
        predicted_val = min(capacity, occupancy + prediction_offset)
        prediction_ratio = predicted_val / capacity if capacity > 0 else 0.0
        confidence_score = 0.50

    predicted_val = max(0, min(capacity, predicted_val))
    return predicted_val, prediction_ratio, confidence_score, current_time

def get_congestion_index(lat: float, lon: float) -> float:
    """
    Consulta la API de TomTom y calcula un índice de congestión del tráfico.
    """

    # Usamos un zoom de 18 para afinar al máximo en la calle del parking
    url = f"https://api.tomtom.com/traffic/services/4/flowSegmentData/absolute/18/json"
    
    params = {
        "key": TOMTOM_KEY,
        "point": f"{lat},{lon}",
        "unit": "KMPH"
    }
    
    try:
        response = requests.get(url, params=params)
        response.raise_for_status()
        data = response.json()
        
        # freeFlowSpeed es la velocidad máxima de la vía sin congestión
        flow_data = data.get("flowSegmentData", {})
        current_speed = flow_data.get("currentSpeed", 0)
        free_flow_speed = flow_data.get("freeFlowSpeed", 0)
        
        if free_flow_speed == 0:
            return 0.0
            
        # Calculamos el índice de congestión
        congestion_index = 1.0 - (current_speed / free_flow_speed)

        # Aseguramos que el valor esté entre 0 y 1
        return max(0.0, min(1.0, congestion_index))
        
    except Exception as e:
        print(f"Error querying TomTom: {e}")
        # Fallback: si la API falla, devolvemos 0 para no romper el modelo
        return 0.0

security = HTTPBearer()

async def get_current_user(credentials: HTTPAuthorizationCredentials = Depends(security)):
    """
    Verifica el JWT de Supabase y devuelve el ID del usuario.
    """
    token = credentials.credentials
    try:
        # Validamos el token con Supabase.
        user = supabase.auth.get_user(token)
        if user:
            return user.user.id
    except Exception as e:
        print(f"Authentication error: {e}")
        raise HTTPException(status_code=401, detail="Invalid or expired token")
    raise HTTPException(status_code=401, detail="Authentication required")

@app.get("/predict")
async def handle_prediction(
    parking_id: str = Query(..., description="ID del parking enviado por la app"),
    user_id: str = Depends(get_current_user)
):
    """
    Endpoint analítico para el Administrador.
    Extrae características, ejecuta el modelo LightGBM y devuelve la ocupación estimada.
    """
    try:
        # Verificar que el usuario autenticado es dueño del parking
        owner_id = get_parking_owner_id(parking_id)
        if owner_id != user_id:
            raise HTTPException(status_code=403, detail="You do not have permission to access this data")

        print(f"\nRunning manual inference request for parking: {parking_id}")
        
        # Obtención de variables de Supabase
        latitude, longitude, capacity, occupancy, _ = get_parking_details(parking_id)
        
        predicted_val, _, confidence_score, current_time = _perform_occupancy_prediction(
            parking_id, latitude, longitude, capacity, occupancy
        )

        return {
            "parkingAreaId": parking_id,
            "dateTime": current_time.isoformat().replace('+00:00', 'Z'),
            "predictedOccupancy": predicted_val,
            "confidenceScore": confidence_score
        }
        
    except HTTPException as http_ex:
        raise http_ex
    except ValueError as ve:
        print(f"Validation error: {ve}")
        raise HTTPException(status_code=400, detail=str(ve))
    except Exception as e:
        print(f"Critical error in prediction pipeline: {e}")
        raise HTTPException(status_code=500, detail="Internal Analytical Server Error")



if __name__ == "__main__":
    uvicorn.run("node:app", host="0.0.0.0", port=8000, reload=True)