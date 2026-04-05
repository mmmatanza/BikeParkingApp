package es.ubu.bikeparkingapp.domain.usecase.user

import es.ubu.bikeparkingapp.domain.repository.AccountRepository

/**
 * Representa el caso de uso para obtener el id de un usuario.
 *
 * @property accountRepository Repositorio de cuentas.
 */
class GetUserIdUseCaseImpl(
    private val accountRepository: AccountRepository
) : GetUserIdUseCase {
    override suspend fun invoke(): Result<String> {
        return try {
            // Llamamos al repositorio para obtener la info del usuario
            val user = accountRepository.getCachedAccount()

            if (user != null) {
                Result.success(user.accountId)
            } else {
                Result.failure(Exception("No user in cache"))
            }
        } catch (e: Exception) {
            // Capturamos errores de red, base de datos, etc.
            Result.failure(e)
        }
    }
}