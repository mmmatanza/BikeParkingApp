package es.ubu.bikeparkingapp.presentation.feature.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.exception.AccountException
import es.ubu.bikeparkingapp.domain.exception.NoActiveSessionException
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import es.ubu.bikeparkingapp.domain.model.AuthState
import es.ubu.bikeparkingapp.domain.usecase.auth.GetAuthStateUseCase
import es.ubu.bikeparkingapp.domain.usecase.auth.LoginUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Representa el ViewModel para la pantalla de inicio de sesión.
 *
 * @property loginUseCase Caso de uso para iniciar sesión.
 * @property getAuthStateUseCase Caso de uso para obtener el estado de autenticación.
 */
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    getAuthStateUseCase: GetAuthStateUseCase
) : ViewModel() {

    private val _state = mutableStateOf(LoginState())
    val state: State<LoginState> = _state
    val authState: Flow<AuthState> = getAuthStateUseCase()



    fun onEmailChange(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun onLoginClick() {
        val email = _state.value.email
        val password = _state.value.password

        viewModelScope.launch {
            loginUseCase(email, password).onFailure { error ->
                val message = when (error) {
                    is NoNetworkException -> "No hay conexión a internet."
                    is NoActiveSessionException -> "Error al generar una sesión activa."
                    is AccountException -> "Error al obtener la cuenta."
                    else -> "Ha ocurrido un error. Revisa tu correo y contraseña e inténtalo de nuevo."
                }
                _state.value = _state.value.copy(error = message)
            }
        }

    }
}