package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.ecodashboard

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.usecase.eco.GetAdminEcoMetricsUseCase
import kotlinx.coroutines.launch

/**
 * ViewModel para el dashboard ecológico de un administrador.
 * @property getAdminEcoMetricsUseCase Caso de uso para obtener las métricas ecológicas del administrador.
 */
class AdminEcoDashboardViewModel(
    private val getAdminEcoMetricsUseCase: GetAdminEcoMetricsUseCase
) : ViewModel() {

    private val _state = mutableStateOf(AdminEcoDashboardState())
    val state: State<AdminEcoDashboardState> = _state

    fun loadMetrics(parkingAreaId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            getAdminEcoMetricsUseCase(parkingAreaId)
                .onSuccess { metrics ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        metrics = metrics
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = error as Exception
                    )
                }
        }
    }

    fun onPeriodSelected(period: EcoPeriod) {
        _state.value = _state.value.copy(selectedPeriod = period)
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
