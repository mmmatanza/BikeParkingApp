package es.ubu.bikeparkingapp.presentation.feature.login
/**
 * Representa el estado de la pantalla de inicio de sesión.
 *
 * @property email Correo electrónico del usuario.
 * @property password Contraseña del usuario.
 * @property isLoading Indica si la pantalla está cargando.
 * @property error Mensaje de error, si ocurre uno.
 */
data class LoginState(
    val email: String = "",
    val password: String = "",
    val error: String? = null
)