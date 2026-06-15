package es.ubu.bikeparkingapp.helper.usecases.eco

import es.ubu.bikeparkingapp.domain.entity.UserEcoMetrics
import es.ubu.bikeparkingapp.domain.usecase.eco.GetUserEcoMetricsUseCase

class FakeGetUserEcoMetricsUseCase : GetUserEcoMetricsUseCase {
    var result: Result<UserEcoMetrics> = Result.failure(NotImplementedError())

    override suspend fun invoke(): Result<UserEcoMetrics> = result
}
