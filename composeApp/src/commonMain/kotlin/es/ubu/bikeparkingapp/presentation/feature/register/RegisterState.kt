package es.ubu.bikeparkingapp.presentation.feature.register

import es.ubu.bikeparkingapp.domain.entity.Role
import es.ubu.bikeparkingapp.domain.exception.RegisterException

/**
 * Representa el estado de la pantalla de registro.
 *
 * @property email Correo electrónico del usuario.
 * @property password Contraseña del usuario.
 * @property passwordConfirmation Confirmación de la contraseña del usuario.
 * @property name Nombre del usuario.
 * @property taxId DNI/NIF/CIF del usuario.
 * @property role Rol del usuario.
 * @property error Mensaje de error, si ocurre uno.
 * @property isSuccess Indica si el registro fue exitoso.
 */
data class RegisterState (
    val email: String = "",
    val password: String = "",
    val passwordConfirmation: String = "",
    val name: String = "",
    val taxId: String = "",
    val role: Role = Role.USER,
    val error: Exception? = null,
    val isSuccess: Boolean = false
)

enum class Role {
    USER,
    ADMIN
}
