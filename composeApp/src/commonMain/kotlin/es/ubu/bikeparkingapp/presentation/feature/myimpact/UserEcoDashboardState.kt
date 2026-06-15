package es.ubu.bikeparkingapp.presentation.feature.myimpact

import es.ubu.bikeparkingapp.domain.entity.UserEcoMetrics
import es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.ecodashboard.EcoPeriod

/**
 * Estado para la pantalla de "Mi Impacto".
 */
data class UserEcoDashboardState(
    val isLoading: Boolean = false,
    val metrics: UserEcoMetrics? = null,
    val selectedPeriod: EcoPeriod = EcoPeriod.WEEK,
    val error: Exception? = null
)
