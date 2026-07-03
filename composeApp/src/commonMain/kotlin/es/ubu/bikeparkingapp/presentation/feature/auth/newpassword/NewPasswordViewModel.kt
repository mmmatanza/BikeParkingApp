package es.ubu.bikeparkingapp.presentation.feature.auth.newpassword

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.usecase.auth.UpdatePasswordUseCase
import es.ubu.bikeparkingapp.presentation.common.util.ErrorMapper
import kotlinx.coroutines.launch

/**
 * Representa el viewModel para la pantalla de nueva contraseña.
 * @property updatePasswordUseCase Caso de uso para actualizar la contraseña.
 */
class NewPasswordViewModel(
    private val updatePasswordUseCase: UpdatePasswordUseCase
) : ViewModel() {

    private val _state = mutableStateOf(NewPasswordState())
    val state: State<NewPasswordState> = _state

    fun onPasswordChange(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    fun onConfirmPasswordChange(password: String) {
        _state.value = _state.value.copy(confirmPassword = password)
    }

    fun onUpdatePasswordClick() {
        val currentState = _state.value
        if (currentState.password != currentState.confirmPassword) {
            _state.value = currentState.copy(error = Exception("password_mismatch"))
            return
        }

        if (currentState.password.isBlank()) {
            _state.value = currentState.copy(error = Exception("password_empty"))
            return
        }

        _state.value = currentState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            // Damos un margen mínimo por si la MainActivity aún está procesando el Deep Link
            kotlinx.coroutines.delay(500) 
            
            updatePasswordUseCase(currentState.password)
                .onSuccess {
                    _state.value = _state.value.copy(isLoading = false, success = true)
                }
                .onFailure { throwable ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = ErrorMapper.map(throwable)
                    )
                }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun clearSuccess() {
        _state.value = _state.value.copy(success = false)
    }
}