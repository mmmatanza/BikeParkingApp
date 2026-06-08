package es.ubu.bikeparkingapp.domain.usecase.theme

/**
 * Interfaz que define el caso de uso para canjear los puntos del usuario
 */
interface RedeemPointsUseCase {
    suspend fun invoke(accountId: String, themeId: String): Result<Unit>
}
