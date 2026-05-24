import os
import uvicorn
from fastapi import FastAPI, Request
from dotenv import load_dotenv
from supabase import create_client, Client
import pandas as pd
import joblib 
import shap
import numpy as np

load_dotenv()

app = FastAPI()

SUPABASE_URL = os.getenv("SUPABASE_URL")
SUPABASE_KEY = os.getenv("SUPABASE_KEY")
supabase: Client = create_client(SUPABASE_URL, SUPABASE_KEY)

MODEL_PATH = "models/random_forest_anomalies.pkl"
rf_model = joblib.load(MODEL_PATH)

# Se inicializa el explainer una sola vez al arrancar
shap_explainer = shap.TreeExplainer(rf_model)

# Features
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
    'reservation_count':                'ABNORMAL_BOOKING_PATTERN',  # como fallback
}

# Obtiene el Dataframe de reservas, excesos de tiempo y la fecha de creación de la cuenta que reserva
def get_user_history(account_id: str):
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

# Obtiene el identificador del dueño del parking para crear la posible alerta
def get_parking_owner_id(parking_area_id: str) -> str:
    query = supabase.table("parkingareas") \
        .select("owner_id") \
        .eq("parking_area_id", parking_area_id) \
        .single() \
        .execute()
    
    if query.data:
        return query.data.get("owner_id")
    raise ValueError(f"Parking not found with ID: {parking_area_id}")

# Elabora el vector de características
def build_feature_vector(new_reservation, df_reservations, df_overstays, account_created_at):
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


# Obtiene el tipo de alerta
def get_alert_type(df_input: pd.DataFrame) -> tuple[str, float]:
    shap_values = shap_explainer.shap_values(df_input)
    anomaly_shap = shap_values[1][0]  # array de contribuciones para esta muestra
    
    # Score de anomalía
    anomaly_score = float(np.sum(anomaly_shap[anomaly_shap > 0]))
    
    # Feature con mayor contribución
    contributions = dict(zip(FEATURE_COLS, anomaly_shap))
    top_feature = max(contributions, key=lambda f: contributions[f])
    
    alert_type = FEATURE_TO_ALERT_TYPE.get(top_feature, 'ABNORMAL_BOOKING_PATTERN')
    return alert_type, anomaly_score

@app.post("/new-reservation")
async def handle_new_reservation(request: Request):
    try:
        payload = await request.json()
        new_reservation = payload.get("record", {})
        
        account_id      = new_reservation.get("account_id")
        reservation_id  = new_reservation.get("reservation_id")
        parking_area_id = new_reservation.get("parking_area_id")
        
        if not account_id or not parking_area_id:
            return {"status": "ignored", "message": "Missing required reservation parameters"}

        print(f"\n--- Processing reservation {reservation_id} for user {account_id} ---")

        # Obtenemos la información del usuario
        df_reservations, df_overstays, account_created_at = get_user_history(account_id)
        
        # Elaboramos la entrada para el modelo 
        features = build_feature_vector(new_reservation, df_reservations, df_overstays, account_created_at)
        print(f"Feature vector: {features}")
        
        # Convertimos la entrada a DataFrame y efectuamos la predicción
        df_input = pd.DataFrame([features])
        prediction = rf_model.predict(df_input)
        is_anomaly = bool(prediction[0])
        
        
        if is_anomaly:
            owner_id = get_parking_owner_id(parking_area_id)
            alert_type, anomaly_score = get_alert_type(df_input)
            print("Anomaly detected")
            supabase.table("alerts").insert({
                "account_id":      owner_id,
                "parking_area_id": parking_area_id,
                "reservation_id":  reservation_id,
                "alert_type":      alert_type,
                "alert_value":     anomaly_score,
                "custom_message":  ""
            }).execute()
            
        else:
            print("Normal booking pattern.")

        # Se devuelve una respuesta satisfactoria a Supabase para que no reintente la petición
        return {
            "status": "success",
            "processed_reservation": reservation_id,
            "anomaly_detected": is_anomaly
        }
        
    except Exception as e:
        print(f"Error processing anomaly analysis: {e}")
        return {"status": "error", "message": str(e)}

if __name__ == "__main__":
    uvicorn.run("node:app", host="0.0.0.0", port=8000, reload=True)