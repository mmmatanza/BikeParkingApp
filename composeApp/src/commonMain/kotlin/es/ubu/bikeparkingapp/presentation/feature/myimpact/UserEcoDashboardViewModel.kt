package es.ubu.bikeparkingapp.presentation.feature.myimpact

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.usecase.eco.GetUserEcoMetricsUseCase
import es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.ecodashboard.EcoPeriod
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de "Mi Impacto".
 */
class UserEcoDashboardViewModel(
    private val getUserEcoMetricsUseCase: GetUserEcoMetricsUseCase
) : ViewModel() {

    private val _state = mutableStateOf(UserEcoDashboardState())
    val state: State<UserEcoDashboardState> = _state

    init {
        loadMetrics()
    }

    fun loadMetrics() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            getUserEcoMetricsUseCase()
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
}
