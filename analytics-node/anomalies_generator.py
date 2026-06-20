import pandas as pd
import numpy as np

RANDOM_STATE = 42
rng = np.random.default_rng(RANDOM_STATE)

N_NORMAL  = 2400
N_ANOMALY = 600  # 20% anomalías

def generate_normal(n):
    return pd.DataFrame({
        'account_age_days':                rng.integers(30, 1800, n),
        'reservation_count':               rng.integers(1, 20, n),
        'cancellation_ratio':              rng.beta(1.5, 8, n),        # mayoría cerca de 0
        'expired_ratio':                   rng.beta(1.2, 10, n),       # muy pocos no-shows
        'avg_overstay_minutes':            rng.gamma(1.5, 4, n),       # overstays cortos ocasionales
        'total_overstays':                 rng.integers(0, 3, n),
        'avg_hours_between_reservations':  rng.gamma(5, 20, n),        # reservas espaciadas
        'booking_hour':                    rng.integers(7, 22, n),     # horas razonables
        'booking_weekday':                 rng.integers(0, 7, n),
        'is_anomaly': 0
    })

def generate_anomaly(n):
    # Mezclamos distintos perfiles anómalos
    n_overstayer  = n // 3        # reincidente en overstays
    n_noshow      = n // 3        # no-shows frecuentes
    n_spammer     = n - 2*(n//3)  # acapara plazas (reservas muy seguidas)

    # Perfil 1: overstayer reincidente
    overstayer = pd.DataFrame({
        'account_age_days':                rng.integers(30, 600, n_overstayer),
        'reservation_count':               rng.integers(10, 20, n_overstayer),
        'cancellation_ratio':              rng.beta(2, 8, n_overstayer),
        'expired_ratio':                   rng.beta(1, 9, n_overstayer),
        'avg_overstay_minutes':            rng.gamma(6, 12, n_overstayer),   # overstays largos
        'total_overstays':                 rng.integers(5, 15, n_overstayer), # muchos overstays
        'avg_hours_between_reservations':  rng.gamma(3, 15, n_overstayer),
        'booking_hour':                    rng.integers(7, 22, n_overstayer),
        'booking_weekday':                 rng.integers(0, 7, n_overstayer),
    })

    # Perfil 2: no-show frecuente
    noshow = pd.DataFrame({
        'account_age_days':                rng.integers(7, 300, n_noshow),   # cuentas nuevas
        'reservation_count':               rng.integers(5, 20, n_noshow),
        'cancellation_ratio':              rng.beta(6, 4, n_noshow),         # muchas cancelaciones
        'expired_ratio':                   rng.beta(5, 3, n_noshow),         # muchos expirados
        'avg_overstay_minutes':            rng.gamma(1, 3, n_noshow),
        'total_overstays':                 rng.integers(0, 3, n_noshow),
        'avg_hours_between_reservations':  rng.gamma(2, 10, n_noshow),
        'booking_hour':                    rng.integers(0, 24, n_noshow),    # horas inusuales
        'booking_weekday':                 rng.integers(0, 7, n_noshow),
    })

    # Perfil 3: acaparador de plazas
    spammer = pd.DataFrame({
        'account_age_days':                rng.integers(1, 60, n_spammer),   # cuenta muy nueva
        'reservation_count':               rng.integers(15, 20, n_spammer),  # muchas reservas
        'cancellation_ratio':              rng.beta(5, 3, n_spammer),        # cancela mucho
        'expired_ratio':                   rng.beta(3, 5, n_spammer),
        'avg_overstay_minutes':            rng.gamma(1, 5, n_spammer),
        'total_overstays':                 rng.integers(0, 4, n_spammer),
        'avg_hours_between_reservations':  rng.uniform(0.1, 2.0, n_spammer), # reservas muy seguidas
        'booking_hour':                    rng.integers(0, 24, n_spammer),
        'booking_weekday':                 rng.integers(0, 7, n_spammer),
    })

    anomalies = pd.concat([overstayer, noshow, spammer], ignore_index=True)
    anomalies['is_anomaly'] = 1
    return anomalies

normal   = generate_normal(N_NORMAL)
anomaly  = generate_anomaly(N_ANOMALY)

df = pd.concat([normal, anomaly], ignore_index=True).sample(frac=1, random_state=RANDOM_STATE).reset_index(drop=True)

# Clamp de valores fuera de rango lógico
df['cancellation_ratio'] = df['cancellation_ratio'].clip(0, 1)
df['expired_ratio']      = df['expired_ratio'].clip(0, 1)
df['avg_overstay_minutes'] = df['avg_overstay_minutes'].clip(0)
df['total_overstays']    = df['total_overstays'].clip(0)
df['booking_hour']       = df['booking_hour'].clip(0, 23)
df['booking_weekday']    = df['booking_weekday'].clip(0, 6)

df.to_csv("data/training_data.csv", index=False)

print(f"Dataset generated: {df.shape[0]} rows")
print(df['is_anomaly'].value_counts().rename({0: 'Normal', 1: 'Anomaly'}))
print(df.describe().round(2))
