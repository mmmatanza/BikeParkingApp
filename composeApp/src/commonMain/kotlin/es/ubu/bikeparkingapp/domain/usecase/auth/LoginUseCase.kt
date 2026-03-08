package es.ubu.bikeparkingapp.domain.usecase.auth

import es.ubu.bikeparkingapp.domain.repository.AccountRepository
import es.ubu.bikeparkingapp.domain.repository.AuthRepository

/**
 * Representa el caso de uso para iniciar sesión.
 *
 * @property authRepository Repositorio de autenticación.
 * @property accountRepository Repositorio de cuentas.
 */
class LoginUseCase(
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(email: String, pass: String):Result<Unit>{
        return runCatching {
            val accountId = authRepository.login(email, pass).getOrThrow()
            val account = accountRepository.getAccount(accountId).getOrThrow()
            accountRepository.saveLocally(account)
        }
    }

}