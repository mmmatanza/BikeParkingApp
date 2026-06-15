package es.ubu.bikeparkingapp.helper.usecases.eco

import es.ubu.bikeparkingapp.domain.entity.AdminEcoMetrics
import es.ubu.bikeparkingapp.domain.usecase.eco.GetAdminEcoMetricsUseCase

class FakeGetAdminEcoMetricsUseCase : GetAdminEcoMetricsUseCase {
    var result: Result<AdminEcoMetrics> = Result.failure(NotImplementedError())

    override suspend fun invoke(parkingAreaId: String): Result<AdminEcoMetrics> = result
}
