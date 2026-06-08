package es.ubu.bikeparkingapp.domain.usecase.theme

import es.ubu.bikeparkingapp.domain.entity.Theme

/**
 * Interfaz que define el caso de uso para obtener
 */
interface GetThemesMarketplaceUseCase {
    suspend fun invoke(accountId: String): Result<List<Theme>>
}
