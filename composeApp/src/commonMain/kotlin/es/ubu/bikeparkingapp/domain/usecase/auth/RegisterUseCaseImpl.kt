package es.ubu.bikeparkingapp.domain.usecase.auth

import es.ubu.bikeparkingapp.domain.entity.Account
import es.ubu.bikeparkingapp.domain.entity.Role
import es.ubu.bikeparkingapp.domain.repository.AccountRepository
import es.ubu.bikeparkingapp.domain.repository.AuthRepository

/**
 * Representa el caso de uso para registrar un usuario.
 *
 * @property authRepository Repositorio de autenticación.
 * @property accountRepository Repositorio de cuentas.
 */

class RegisterUseCaseImpl(
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository
): RegisterUseCase {
    override suspend operator fun invoke(
        email: String,
        password: String,
        name: String,
        taxId: String,
        role: Role
    ): Result<Account> {
        return runCatching {
            // Crea el usuario en Supabase Auth
            val userId = authRepository.register(
                email = email,
                password = password
            ).getOrThrow()

            // Crea el perfil en la tabla accounts
            val account = accountRepository.createAccount(
                userId = userId,
                name = name,
                taxId = taxId,
                role = role
            ).getOrThrow()

            authRepository.signout().getOrThrow()

            account
        }
    }
}