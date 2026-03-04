package es.ubu.bikeparkingapp.presentation.feature.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.usecase.LoginUseCase
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = mutableStateOf(LoginState())
    val state: State<LoginState> = _state

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

        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {

            val result = loginUseCase(email, password)
            result.onSuccess {
                _state.value = _state.value.copy(isLoading = false, isLoggedIn = true)
            }.onFailure { error ->
                _state.value = _state.value.copy(isLoading = false)
                _state.value = _state.value.copy(error = error.message)
            }
        }

    }
}