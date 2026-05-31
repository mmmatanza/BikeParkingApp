package es.ubu.bikeparkingapp.helper.repositories

import es.ubu.bikeparkingapp.domain.repository.OccupancyRepository

class FakeOccupancyRepository : OccupancyRepository {
    var response: Int = 0
    var shouldReturnError = false

    override suspend fun getPredictedOccupancy(parkingAreaId: String): Result<Int> {
        return if (shouldReturnError) {
            Result.failure(Exception("Occupancy prediction error"))
        } else {
            Result.success(response)
        }
    }
}
