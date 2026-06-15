package es.ubu.bikeparkingapp.helper.usecases.user

import es.ubu.bikeparkingapp.domain.usecase.user.GetUserPointsUseCase

class FakeGetUserPointsUseCase : GetUserPointsUseCase {
    var points = 0
    var shouldReturnError = false

    override suspend fun invoke(accountId: String): Result<Int> {
        return if (shouldReturnError) Result.failure(Exception("Error"))
        else Result.success(points)
    }
}
