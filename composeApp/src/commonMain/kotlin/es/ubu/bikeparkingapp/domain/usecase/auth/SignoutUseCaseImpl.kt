package es.ubu.bikeparkingapp.domain.usecase.auth

import es.ubu.bikeparkingapp.domain.repository.AuthRepository

/**
 * Representa el caso de uso para cerrar sesión.
 *
 * @property authRepository Repositorio de autenticación.
 */
class SignoutUseCaseImpl(private val authRepository: AuthRepository): SignoutUseCase {
    override suspend operator fun invoke() =
        authRepository.signout()
}