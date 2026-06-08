package es.ubu.bikeparkingapp.domain.usecase.user

/**
 * Interfaz que define el caso de uso para obtener los puntos del usuario
 */
interface GetUserPointsUseCase {
    suspend fun invoke(accountId: String): Result<Int>
}
