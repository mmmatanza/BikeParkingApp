package es.ubu.bikeparkingapp.domain.usecase.auth

import es.ubu.bikeparkingapp.domain.repository.AuthRepository

/**
 * Representa el caso de uso para iniciar sesión.
 *
 * @property authRepository Repositorio de autenticación.
 */
class LoginUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, pass: String) =
        authRepository.login(email, pass)
}