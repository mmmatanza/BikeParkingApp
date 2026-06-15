package es.ubu.bikeparkingapp.helper.usecases.theme

import es.ubu.bikeparkingapp.domain.entity.Theme
import es.ubu.bikeparkingapp.domain.usecase.theme.GetThemesMarketplaceUseCase

class FakeGetThemesMarketplaceUseCase : GetThemesMarketplaceUseCase {
    var result: Result<List<Theme>> = Result.failure(NotImplementedError())
    override suspend fun invoke(accountId: String): Result<List<Theme>> = result
}
