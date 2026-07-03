package es.ubu.bikeparkingapp.presentation.feature.auth.newpassword

/**
 * Representa el estado de la pantalla de nueva contraseña.
 * @property error Mensaje de error, si ocurre uno.
 * @property password Nueva contraseña.
 * @property confirmPassword Confirmación de la nueva contraseña.
 * @property success Indica si el proceso de actualización de contraseña fue exitoso.
 * @property isLoading Indica si se está realizando una operación de carga.
 */
data class NewPasswordState(
    val error: Exception? = null,
    val password: String = "",
    val confirmPassword: String = "",
    val success: Boolean = false,
    val isLoading: Boolean = false
)