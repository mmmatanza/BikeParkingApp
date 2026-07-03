package es.ubu.bikeparkingapp.domain.usecase.auth

import es.ubu.bikeparkingapp.domain.repository.AuthRepository

/**
 * Implementación del caso de uso para actualizar la contraseña.
 * @property repository Repositorio de autenticación.
 */
class UpdatePasswordUseCaseImpl(
    private val repository: AuthRepository
) : UpdatePasswordUseCase {
    override suspend fun invoke(newPassword: String): Result<Unit> =
        repository.updatePassword(newPassword)
}
