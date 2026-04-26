package es.ubu.bikeparkingapp.domain.usecase.user

import es.ubu.bikeparkingapp.domain.entity.Role
import es.ubu.bikeparkingapp.domain.repository.AccountRepository


/**
 * Representa el caso de uso para obtener el rol de un usuario.
 * @property accountRepository Repositorio de cuentas.
 * @return El rol del usuario.
 */
class GetUserRoleUseCaseImpl(
    private val accountRepository: AccountRepository
) : GetUserRoleUseCase {
    override suspend fun invoke(): Result<Role> {
        return try {
            // Llamamos al repositorio para obtener la info del usuario
            val user = accountRepository.getCachedAccount()

            if (user != null) {
                Result.success(user.role)
            } else {
                Result.failure(Exception("No user in cache"))
            }
        } catch (e: Exception) {
            // Capturamos errores de red, base de datos, etc.
            Result.failure(e)
        }
    }
}