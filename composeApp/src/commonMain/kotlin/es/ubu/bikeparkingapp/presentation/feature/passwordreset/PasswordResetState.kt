package es.ubu.bikeparkingapp.presentation.feature.passwordreset

/**
 * Representa el estado de la pantalla de restablecer contraseña.
 * @property error Mensaje de error, si ocurre uno.
 * @property email Correo electrónico del usuario.
 * @property success Indica si el proceso de restablecimiento de contraseña fue exitoso.
 */
data class PasswordResetState (
    val error: String? = null,
    val email: String = "",
    val success: Boolean=false
)