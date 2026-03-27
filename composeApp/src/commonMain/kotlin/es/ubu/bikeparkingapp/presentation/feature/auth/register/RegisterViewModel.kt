package es.ubu.bikeparkingapp.presentation.feature.auth.register

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.entity.Role
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import es.ubu.bikeparkingapp.domain.exception.RegisterException.*
import es.ubu.bikeparkingapp.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.launch
import es.ubu.bikeparkingapp.domain.exception.EmailInvalidException


/**
 * Representa el viewModel para la pantalla de registro.
 *
 * @property registerUseCase Caso de uso para registrar un usuario.
 */
class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    private val _state = mutableStateOf(RegisterState())
    val state: State<RegisterState> = _state

    fun onEmailChange(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    fun onPasswordConfirmationChange(passwordConfirmation: String) {
        _state.value = _state.value.copy(passwordConfirmation = passwordConfirmation)
    }

    fun onNameChange(name: String) {
        _state.value = _state.value.copy(name = name)
    }

    fun onTaxIdChange(taxId: String) {
        _state.value = _state.value.copy(taxId = taxId)
    }

    fun onRoleChange(role: Role) {
        _state.value = _state.value.copy(role = role)
    }

    // Función para validar el formulario
    private fun validate(): Int? {
        val state = _state.value

        if (state.name.isBlank()) throw NameEmptyException()
        if (!isValidEmail(state.email)) throw EmailInvalidException()
        if (state.taxId.isBlank()) throw TaxIdEmptyException()
        if (state.password != state.passwordConfirmation) throw PasswordMismatchException()
        if (!isValidPassword(state.password)) throw WeakPasswordException()

        return null
    }

    // Función para verificar si un correo electrónico es válido
    private fun isValidEmail(email: String): Boolean {
        return Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(email)
    }

    // Función para verificar que la contraseña no es débil
    private fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        if (!password.any { it.isUpperCase() }) return false
        if (!password.any { it in "!@#\$%^&*()_+-=[]{}|;':\",./<>?" }) return false
        return true
    }

    fun onRegisterClick(){
        try{
            validate()
            viewModelScope.launch{
                registerUseCase(
                    email = _state.value.email,
                    password = _state.value.password,
                    name = _state.value.name,
                    taxId = _state.value.taxId,
                    role = _state.value.role
                ).onFailure { error ->
                    when (error) {
                        is NoNetworkException -> _state.value = _state.value.copy(error = error)
                        else -> _state.value = _state.value.copy(error = Exception(error.message))
                    }
                }.onSuccess {
                    _state.value = _state.value.copy(isSuccess = true)
                }
            }
        } catch (e: Exception) {
            _state.value = _state.value.copy(error = e)
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

}