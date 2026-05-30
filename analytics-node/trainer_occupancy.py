import os
import numpy as np
import pandas as pd
import joblib
import lightgbm as lgb
import matplotlib.pyplot as plt
from sklearn.model_selection import TimeSeriesSplit
from sklearn.metrics import mean_absolute_error, mean_squared_error

# Rutas de archivos
DATA_PATH = "data/occupancy_dataset.csv"
MODEL_DIR = "models"
MODEL_OUTPUT_PATH = os.path.join(MODEL_DIR, "lightgbm_occupancy.pkl")

os.makedirs(MODEL_DIR, exist_ok=True)

if not os.path.exists(DATA_PATH):
    raise FileNotFoundError(f"No se encuentra el archivo de datos")

print("Loading data")
df_raw = pd.read_csv(DATA_PATH)

# Asegura el orden cronológico para el desplazamiento temporal
df_raw['timestamp'] = pd.to_datetime(df_raw['timestamp'])
df_raw = df_raw.sort_values(by=['parking_id', 'timestamp']).reset_index(drop=True)

# Creación del target
df_raw['target_occupancy_ratio'] = df_raw.groupby('parking_id')['current_occupancy_ratio'].shift(-2)

# Eliminamos las filas que no tienen "futuro"
df_raw = df_raw.dropna(subset=['target_occupancy_ratio'])

FEATURE_COLS = [
    'current_occupancy_ratio',  # Ratio actual (T)
    'traffic_congestion_index', # Tráfico actual (T)
    'booking_hour',
    'booking_weekday',
    'capacity'
]
TARGET_COL = 'target_occupancy_ratio' # Ratio futuro (T + 1h)

X = df_raw[FEATURE_COLS]
y = df_raw[TARGET_COL]


print("\nStarting temporal cross-validation")
tscv = TimeSeriesSplit(n_splits=3)
maes = []

for fold, (train_index, test_index) in enumerate(tscv.split(X)):
    X_train, X_test = X.iloc[train_index], X.iloc[test_index]
    y_train, y_test = y.iloc[train_index], y.iloc[test_index]
    
    model_fold = lgb.LGBMRegressor(
        n_estimators=100,
        learning_rate=0.05,
        max_depth=6,
        random_state=42,
        verbosity=-1
    )
    
    model_fold.fit(X_train, y_train)
    predictions = model_fold.predict(X_test)
    
    mae = mean_absolute_error(y_test, predictions)
    maes.append(mae)
    print(f"Fold {fold+1} -> MAE: {mae:.4f}")

print(f"\nGlobal Mean Absolute Error (MAE): {np.mean(maes):.4f}")

# Generación de la figura de rendimiento
print("\nGenerating evaluation plots...")
fig, axes = plt.subplots(1, 3, figsize=(18, 5))

# Predicho vs real
axes[0].scatter(y_test, predictions, alpha=0.5, color='teal')
axes[0].plot([y_test.min(), y_test.max()], [y_test.min(), y_test.max()], 'r--', lw=2)
axes[0].set_title("Actual vs Predicted Ratio")
axes[0].set_xlabel("Actual Ratio")
axes[0].set_ylabel("Predicted Ratio")

# Importancia de características
lgb.plot_importance(model_fold, ax=axes[1], importance_type='gain', title='Feature Importance (Gain)')

# Histograma del error
errors = predictions - y_test
axes[2].hist(errors, bins=30, color='skyblue', edgecolor='black')
axes[2].axvline(x=0, color='red', linestyle='--')
axes[2].set_title("Prediction Error Distribution")
axes[2].set_xlabel("Error (Pred - Actual)")
axes[2].set_ylabel("Frequency")

plt.tight_layout()
plt.savefig(os.path.join(MODEL_DIR, "occupancy_evaluation.png"), dpi=150)
print(f"Plot saved to {os.path.join(MODEL_DIR, 'occupancy_evaluation.png')}")

print("\nTraining final model")
final_model = lgb.LGBMRegressor(
    n_estimators=120,
    learning_rate=0.05,
    max_depth=6,
    random_state=42,
    verbosity=-1
)
final_model.fit(X, y)

joblib.dump(final_model, MODEL_OUTPUT_PATH)
print(f"Model saved successfully")
