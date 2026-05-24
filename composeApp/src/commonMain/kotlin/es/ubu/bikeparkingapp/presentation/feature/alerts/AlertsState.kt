package es.ubu.bikeparkingapp.presentation.feature.alerts

import es.ubu.bikeparkingapp.domain.entity.Alert

/**
 * Representa el estado de la pantalla de alertas.
 */
data class AlertsState(
    val alerts: List<Alert> = emptyList(),
    val isLoading: Boolean = false,
    val error: Exception? = null
)
