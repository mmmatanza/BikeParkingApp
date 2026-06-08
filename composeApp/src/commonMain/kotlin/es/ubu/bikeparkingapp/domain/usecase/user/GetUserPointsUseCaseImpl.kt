package es.ubu.bikeparkingapp.domain.usecase.user

import es.ubu.bikeparkingapp.domain.repository.AccountRepository

/**
 * Clase que implementa el caso de uso para obtener los puntos del usuario
 */
class GetUserPointsUseCaseImpl(
    private val accountRepository: AccountRepository
) : GetUserPointsUseCase {
    override suspend fun invoke(accountId: String): Result<Int> = runCatching {
        val account = accountRepository.getAccount(accountId).getOrThrow()
        account.points
    }
}
