package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.publishalert

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.usecase.alert.PublishParkingAlertUseCase
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de emitir alerta.
 * @property publishParkingAlertUseCase Caso de uso para publicar una alerta.
 */
class PublishAlertViewModel(
    private val publishParkingAlertUseCase: PublishParkingAlertUseCase
) : ViewModel() {

    private val _state = mutableStateOf(PublishAlertState())
    val state: State<PublishAlertState> = _state

    fun onMessageChange(message: String) {
        _state.value = _state.value.copy(message = message)
    }

    fun onPublishAlert(parkingId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            publishParkingAlertUseCase(parkingId, _state.value.message)
                .onSuccess {
                    _state.value = _state.value.copy(isLoading = false, isSuccess = true)
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(isLoading = false, error = error)
                }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun clearState() {
        _state.value = PublishAlertState()
    }
}
