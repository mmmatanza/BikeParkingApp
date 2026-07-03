package es.ubu.bikeparkingapp.domain.usecase.auth

/**
 * Caso de uso para actualizar la contraseña del usuario.
 */
interface UpdatePasswordUseCase {
    suspend operator fun invoke(newPassword: String): Result<Unit>
}
