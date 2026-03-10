package es.ubu.bikeparkingapp.presentation.feature.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.exception.EmailInvalidException
import es.ubu.bikeparkingapp.domain.exception.InvalidCredentialsException
import es.ubu.bikeparkingapp.domain.exception.NoActiveSessionException
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import es.ubu.bikeparkingapp.domain.exception.PasswordEmptyException
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

    private fun isValidEmail(email: String): Boolean {
        return Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(email)
    }

    fun validate() {

        val email = _state.value.email
        val password = _state.value.password

        if (!isValidEmail(email)) {
            _state.value = _state.value.copy(error = EmailInvalidException())
            return
        }

        if (password.isBlank()) {
            _state.value = _state.value.copy(error = PasswordEmptyException())
            return
        }
    }

    fun onLoginClick() {
        try {
            val email = _state.value.email
            val password = _state.value.password
            validate()
            viewModelScope.launch {
                loginUseCase(email, password).onFailure { error ->
                    when (error) {
                        is NoNetworkException -> _state.value = _state.value.copy(error = error)
                        is NoActiveSessionException -> _state.value = _state.value.copy(error = error)
                        is InvalidCredentialsException -> _state.value = _state.value.copy(error = error)
                        else -> _state.value = _state.value.copy(error = Exception(error.message))
                    }
                }
            }
        } catch (exception: Exception) {
            _state.value.copy(error = exception)
        }
    }


}
