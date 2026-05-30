package es.ubu.bikeparkingapp.helper.usecases.occupancy

import es.ubu.bikeparkingapp.domain.usecase.occupancy.GetPredictedOccupancyUseCase

class FakeGetPredictedOccupancyUseCase : GetPredictedOccupancyUseCase {
    var response: Int = 0
    var shouldReturnError = false

    override suspend fun invoke(parkingAreaId: String): Result<Int> {
        return if (shouldReturnError) {
            Result.failure(Exception("Prediction error"))
        } else {
            Result.success(response)
        }
    }
}
