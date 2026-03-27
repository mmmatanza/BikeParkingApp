package es.ubu.bikeparkingapp.presentation.feature.auth.passwordreset

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import es.ubu.bikeparkingapp.domain.usecase.auth.RequestPasswordResetUseCase
import kotlinx.coroutines.launch
import es.ubu.bikeparkingapp.domain.exception.EmailInvalidException

/**
 * Representa el viewModel para la pantalla de restablecer contraseña.
 * @property requestPasswordResetUseCase Caso de uso para restablecer la contraseña.
 *
 */
class PasswordResetViewModel(
    private val requestPasswordResetUseCase: RequestPasswordResetUseCase
) : ViewModel() {

    private val _state = mutableStateOf(PasswordResetState())
    val state: State<PasswordResetState> = _state

    // Función para validar el formulario
    private fun validate(): String? {
        val state = _state.value

        if (!isValidEmail(state.email)) throw EmailInvalidException()

        return null
    }

    // Función para verificar si un correo electrónico es válido
    private fun isValidEmail(email: String): Boolean {
        return Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(email)
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun clearSuccess() {
        _state.value = _state.value.copy(success = false)
    }

    fun onPasswordResetClick(){
        try {
            validate()
            viewModelScope.launch{
                requestPasswordResetUseCase(
                    email = _state.value.email
                ).onFailure { error ->
                    when (error) {
                        is NoNetworkException -> _state.value = _state.value.copy(error = error)
                        else -> _state.value = _state.value.copy(error = Exception(error.message))
                    }
                }.onSuccess {
                    _state.value = _state.value.copy(success = true)
                }
            }
        } catch (exception: EmailInvalidException){
            _state.value = _state.value.copy(error = exception)
        }
    }

    fun onEmailChange(email: String){
        _state.value = _state.value.copy(email = email)
    }

}