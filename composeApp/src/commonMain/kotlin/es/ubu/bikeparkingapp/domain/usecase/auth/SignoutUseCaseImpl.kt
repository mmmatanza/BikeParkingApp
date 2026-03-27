package es.ubu.bikeparkingapp.domain.usecase.auth

import es.ubu.bikeparkingapp.domain.repository.AccountRepository
import es.ubu.bikeparkingapp.domain.repository.AuthRepository

/**
 * Representa el caso de uso para cerrar sesión.
 *
 * @property authRepository Repositorio de autenticación.
 */
class SignoutUseCaseImpl(
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository
): SignoutUseCase {
    override suspend operator fun invoke(): Result<Unit> {
        // Limpiamos los datos locales
        accountRepository.clearAccount()

        // Cerramos la sesión
        return authRepository.signout()
    }
}