import pandas as pd
import numpy as np
import joblib
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split, StratifiedKFold, cross_val_score
from sklearn.metrics import (
    classification_report,
    confusion_matrix,
    roc_auc_score,
    ConfusionMatrixDisplay
)
import matplotlib.pyplot as plt
import os

# Configuraciones
DATA_PATH  = "data/training_data.csv"
MODEL_DIR  = "models"
MODEL_PATH = os.path.join(MODEL_DIR, "random_forest_anomalies.pkl")

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
TARGET_COL = 'is_anomaly'

RANDOM_STATE = 42
N_CV_FOLDS   = 5

# Carga del archivo de datos
df = pd.read_csv(DATA_PATH)

print(f"Dataset shape: {df.shape}")
print(f"Class distribution:\n{df[TARGET_COL].value_counts()}\n")

X = df[FEATURE_COLS]
y = df[TARGET_COL]

# Separación entre conjunto de entrenamiento y prueba
X_train, X_test, y_train, y_test = train_test_split(
    X, y, test_size=0.2, stratify=y, random_state=RANDOM_STATE
)

# Modelo
model = RandomForestClassifier(
    n_estimators=200, # Número de árboles de decisión
    max_depth=None,
    min_samples_leaf=2,
    class_weight='balanced',
    random_state=RANDOM_STATE,
    n_jobs=-1,
)

model.fit(X_train, y_train)

# Cross-validation

cv = StratifiedKFold(n_splits=N_CV_FOLDS, shuffle=True, random_state=RANDOM_STATE)

cv_accuracy  = cross_val_score(model, X, y, cv=cv, scoring='accuracy') # % de predicciones correctas
cv_f1        = cross_val_score(model, X, y, cv=cv, scoring='f1') # media armónica de precision y recall
cv_roc_auc   = cross_val_score(model, X, y, cv=cv, scoring='roc_auc') # mide la capacidad del modelo de distinguir entre clases
cv_precision = cross_val_score(model, X, y, cv=cv, scoring='precision') # 
cv_recall    = cross_val_score(model, X, y, cv=cv, scoring='recall') # Alto recall = pocos casos anómalos sin detectar.

# Evaluación del conjunto de prueba

y_pred      = model.predict(X_test)
y_pred_prob = model.predict_proba(X_test)[:, 1]

print("=" * 55)
print("  CROSS-VALIDATION RESULTS  (k={})".format(N_CV_FOLDS))
print("=" * 55)
for name, scores in [
    ("Accuracy",  cv_accuracy),
    ("Precision", cv_precision),
    ("Recall",    cv_recall),
    ("F1",        cv_f1),
    ("ROC-AUC",   cv_roc_auc),
]:
    print(f"  {name:<12}  mean={scores.mean():.4f}  std={scores.std():.4f}"
          f"  min={scores.min():.4f}  max={scores.max():.4f}")

print()
print("=" * 55)
print("  TEST SET RESULTS")
print("=" * 55)
print(classification_report(y_test, y_pred, target_names=["Normal", "Anomaly"]))
print(f"  ROC-AUC: {roc_auc_score(y_test, y_pred_prob):.4f}")

print()
print("=" * 55)
print("  FEATURE IMPORTANCES")
print("=" * 55)
importances = pd.Series(model.feature_importances_, index=FEATURE_COLS).sort_values(ascending=False)
for feat, imp in importances.items():
    bar = "█" * int(imp * 50)
    print(f"  {feat:<38} {imp:.4f}  {bar}")

# Gráficos
fig, axes = plt.subplots(1, 3, figsize=(18, 5))

# Matriz de confusión
ConfusionMatrixDisplay(
    confusion_matrix(y_test, y_pred),
    display_labels=["Normal", "Anomaly"]
).plot(ax=axes[0], colorbar=False)
axes[0].set_title("Confusion Matrix (test set)")

# Puntuaciones de cv por partición
metrics_cv = {
    "Accuracy":  cv_accuracy,
    "Precision": cv_precision,
    "Recall":    cv_recall,
    "F1":        cv_f1,
    "ROC-AUC":   cv_roc_auc,
}
x = np.arange(N_CV_FOLDS)
width = 0.15
for i, (name, scores) in enumerate(metrics_cv.items()):
    axes[1].bar(x + i * width, scores, width, label=name)
axes[1].set_xticks(x + width * 2)
axes[1].set_xticklabels([f"Fold {i+1}" for i in range(N_CV_FOLDS)])
axes[1].set_ylim(0, 1.1)
axes[1].set_title("CV Scores per Fold")
axes[1].legend(fontsize=7)

# Importancia de las características
importances.plot(kind='barh', ax=axes[2])
axes[2].invert_yaxis()
axes[2].set_title("Feature Importances")
axes[2].set_xlabel("Importance")

plt.tight_layout()
plt.savefig("models/evaluation.png", dpi=150)
plt.show()

# Guardamos el modelo
os.makedirs(MODEL_DIR, exist_ok=True)
joblib.dump(model, MODEL_PATH)
print(f"\nModel saved to {MODEL_PATH}")
