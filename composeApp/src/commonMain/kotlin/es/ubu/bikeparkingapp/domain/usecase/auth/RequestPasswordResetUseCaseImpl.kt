package es.ubu.bikeparkingapp.domain.usecase.auth

import es.ubu.bikeparkingapp.domain.repository.AuthRepository

/**
 * Representa el caso de uso para restablecer la contraseña.
 * @property authRepository Repositorio de autenticación.
 */
class RequestPasswordResetUseCaseImpl(
    private val authRepository: AuthRepository,
): RequestPasswordResetUseCase {
    override suspend operator fun invoke(email:String) = authRepository.requestPasswordReset(email)
}