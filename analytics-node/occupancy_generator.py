import os
import numpy as np
import pandas as pd

# Asegura que existe la carpeta para guardar los datos
os.makedirs("data", exist_ok=True)

# Configuración de la simulación
N_DAYS = 60            # 2 meses de histórico
INTERVALS_PER_DAY = 48  # Fotos fijas cada 30 minutos
TOTAL_RECORDS = N_DAYS * INTERVALS_PER_DAY
N_PARKINGS = 5         
CAPACITIES = [15, 26, 40, 50, 100]

print("Starting generation")
np.random.seed(42)

# Línea temporal continua
date_range = pd.date_range(start="2026-01-01", periods=TOTAL_RECORDS, freq="30min", tz="UTC")
dataset_list = []

for parking_idx in range(N_PARKINGS):
    capacity = CAPACITIES[parking_idx]
    is_office_zone = (parking_idx % 2 == 0)
    
    for timestamp in date_range:
        hour = timestamp.hour
        weekday = timestamp.dayofweek
        
        # Patrón de ocupación base
        if is_office_zone:
            if weekday < 5:  # Lunes a viernes (en horario laboral)
                base_ratio = 0.85 if (8 <= hour <= 17) else 0.15
            else:  # Fin de semana vacío
                base_ratio = 0.05
        else:
            if weekday >= 4:  # Jueves a domingo (tardes/noches de ocio)
                base_ratio = 0.80 if (18 <= hour <= 23) else 0.25
            else:
                base_ratio = 0.20
        
        # Simulación de tráfico
        if weekday < 5 and (hour == 8 or hour == 18):
            traffic_index = np.random.uniform(0.65, 0.95)
        else:
            traffic_index = np.random.uniform(0.0, 0.35)
            
        # Correlación -> el tráfico alto empuja el uso de la bici
        if traffic_index > 0.6:
            base_ratio = min(0.98, base_ratio + 0.10)
            
        # Ruido gaussiano
        final_ratio = base_ratio + np.random.normal(0, 0.04)
        final_ratio = max(0.0, min(1.0, final_ratio))
        
        dataset_list.append({
            'parking_id': f"parking_sim_{parking_idx}",
            'capacity': capacity,
            'booking_hour': hour,
            'booking_weekday': weekday,
            'traffic_congestion_index': traffic_index,
            'current_occupancy_ratio': final_ratio,
            'timestamp': timestamp.isoformat()
        })

df = pd.DataFrame(dataset_list)
output_path = "data/occupancy_dataset.csv"
df.to_csv(output_path, index=False)

print("Data saved successfully")
