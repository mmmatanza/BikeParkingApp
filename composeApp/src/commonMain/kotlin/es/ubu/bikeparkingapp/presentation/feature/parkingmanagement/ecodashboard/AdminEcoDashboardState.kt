package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.ecodashboard

import es.ubu.bikeparkingapp.domain.entity.AdminEcoMetrics

/**
 * Enumerado que representa los diferentes períodos de métricas ecológicas.
 */
enum class EcoPeriod {
    WEEK, MONTH, YEAR
}

/**
 * Representa el estado del dashboard ecológico de un administrador.
 * @property isLoading Indica si se está cargando la información.
 * @property metrics Información sobre las métricas ecológicas.
 * @property selectedPeriod Período seleccionado para las métricas.
 * @property error Error ocurrido durante la carga de la información.
 */
data class AdminEcoDashboardState(
    val isLoading: Boolean = false,
    val metrics: AdminEcoMetrics? = null,
    val selectedPeriod: EcoPeriod = EcoPeriod.WEEK,
    val error: Exception? = null
)
