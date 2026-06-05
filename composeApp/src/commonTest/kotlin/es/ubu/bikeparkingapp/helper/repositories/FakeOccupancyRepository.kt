package es.ubu.bikeparkingapp.helper.repositories

import es.ubu.bikeparkingapp.domain.entity.OccupancyPrediction
import es.ubu.bikeparkingapp.domain.repository.OccupancyRepository
import kotlin.time.Instant

class FakeOccupancyRepository : OccupancyRepository {
    var response: OccupancyPrediction = OccupancyPrediction("", Instant.fromEpochMilliseconds(0), 0, 0.0)
    var shouldReturnError = false

    override suspend fun getPredictedOccupancy(parkingAreaId: String): Result<OccupancyPrediction> {
        return if (shouldReturnError) {
            Result.failure(Exception("Occupancy prediction error"))
        } else {
            Result.success(response)
        }
    }
}
