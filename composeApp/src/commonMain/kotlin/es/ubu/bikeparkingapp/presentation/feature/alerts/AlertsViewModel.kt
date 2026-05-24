package es.ubu.bikeparkingapp.presentation.feature.alerts

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.entity.Alert
import es.ubu.bikeparkingapp.domain.usecase.alert.GetAlertsUseCase
import es.ubu.bikeparkingapp.domain.usecase.alert.MarkAlertAsReadUseCase
import es.ubu.bikeparkingapp.domain.usecase.alert.MarkAllAlertsAsReadUseCase
import es.ubu.bikeparkingapp.presentation.common.util.ErrorMapper
import kotlinx.coroutines.launch

/**
 * ViewModel para la gestión de alertas.
 */
class AlertsViewModel(
    private val getAlertsUseCase: GetAlertsUseCase,
    private val markAlertAsReadUseCase: MarkAlertAsReadUseCase,
    private val markAllAlertsAsReadUseCase: MarkAllAlertsAsReadUseCase
) : ViewModel() {

    private val _state = mutableStateOf(AlertsState())
    val state: State<AlertsState> = _state

    fun clearState() {
        _state.value = AlertsState()
    }

    fun loadAlerts() {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            getAlertsUseCase()
                .onSuccess { alerts ->
                    _state.value = _state.value.copy(
                        alerts = sortAlerts(alerts),
                        isLoading = false,
                        error = null
                    )
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = ErrorMapper.map(it)
                    )
                }
        }
    }

    fun markAsRead(alertId: String) {
        viewModelScope.launch {
            markAlertAsReadUseCase(alertId)
                .onSuccess {
                    val updatedAlerts = _state.value.alerts.map {
                        if (it.alertId == alertId) it.copy(isRead = true) else it
                    }
                    _state.value = _state.value.copy(
                        alerts = sortAlerts(updatedAlerts)
                    )
                }
                .onFailure {
                    _state.value = _state.value.copy(error = ErrorMapper.map(it))
                }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            markAllAlertsAsReadUseCase()
                .onSuccess {
                    val updatedAlerts = _state.value.alerts.map { it.copy(isRead = true) }
                    _state.value = _state.value.copy(
                        alerts = sortAlerts(updatedAlerts)
                    )
                }
                .onFailure {
                    _state.value = _state.value.copy(error = ErrorMapper.map(it))
                }
        }
    }

    private fun sortAlerts(alerts: List<Alert>): List<Alert> {
        return alerts.sortedWith(
            compareBy<Alert> { it.isRead }
                .thenByDescending { it.createdAt }
        )
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
