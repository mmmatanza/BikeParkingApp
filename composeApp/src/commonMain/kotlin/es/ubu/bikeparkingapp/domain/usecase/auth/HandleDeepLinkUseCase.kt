package es.ubu.bikeparkingapp.domain.usecase.auth

/**
 * Caso de uso para manejar deep links de autenticación (ej: restablecimiento de contraseña).
 */
interface HandleDeepLinkUseCase {
    suspend operator fun invoke(url: String): Result<Unit>
}
