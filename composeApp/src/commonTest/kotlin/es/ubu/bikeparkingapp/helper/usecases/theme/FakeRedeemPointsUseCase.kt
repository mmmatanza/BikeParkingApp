package es.ubu.bikeparkingapp.helper.usecases.theme

import es.ubu.bikeparkingapp.domain.usecase.theme.RedeemPointsUseCase

class FakeRedeemPointsUseCase : RedeemPointsUseCase {
    var result: Result<Unit> = Result.success(Unit)
    override suspend fun invoke(accountId: String, themeId: String): Result<Unit> = result
}
