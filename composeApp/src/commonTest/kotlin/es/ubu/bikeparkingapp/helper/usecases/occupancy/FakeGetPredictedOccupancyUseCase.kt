package es.ubu.bikeparkingapp.helper.usecases.occupancy

import es.ubu.bikeparkingapp.domain.entity.OccupancyPrediction
import es.ubu.bikeparkingapp.domain.usecase.occupancy.GetPredictedOccupancyUseCase
import kotlin.time.Instant

class FakeGetPredictedOccupancyUseCase : GetPredictedOccupancyUseCase {
    var response: OccupancyPrediction = OccupancyPrediction("", Instant.fromEpochMilliseconds(0), 0, 0.0)
    var shouldReturnError = false

    override suspend fun invoke(parkingAreaId: String): Result<OccupancyPrediction> {
        return if (shouldReturnError) {
            Result.failure(Exception("Prediction error"))
        } else {
            Result.success(response)
        }
    }
}
