package es.ubu.bikeparkingapp.presentation.feature.register

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.entity.Role
import es.ubu.bikeparkingapp.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.launch


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
    private fun validate(): String? {
        val state = _state.value

        if (state.name.isBlank()) return "El nombre no puede estar vacío."

        if (!isValidEmail(state.email)) return "El correo electrónico no es válido."

        if (state.taxId.isBlank()) return "El NIF no puede estar vacío."

        if (state.password != state.passwordConfirmation) return "Las contraseñas no coinciden."

        return null
    }

    // Función para verificar si un correo electrónico es válido
    private fun isValidEmail(email: String): Boolean {
        return Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(email)
    }

    fun onRegisterClick(){
        val validationError = validate()
        if (validationError != null) {
            _state.value = _state.value.copy(error = validationError)
            return
        }
        viewModelScope.launch{
            val result = registerUseCase(
                email = _state.value.email,
                password = _state.value.password,
                name = _state.value.name,
                taxId = _state.value.taxId,
                role = _state.value.role
            )
            if(result.isFailure){
                _state.value = _state.value.copy(error = result.exceptionOrNull()?.message)
            } else{
                _state.value = _state.value.copy(isSuccess = true)
            }
        }

    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

}