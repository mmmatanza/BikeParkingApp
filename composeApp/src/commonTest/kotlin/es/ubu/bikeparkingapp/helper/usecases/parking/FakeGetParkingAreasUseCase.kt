package es.ubu.bikeparkingapp.helper.usecases.parking

import es.ubu.bikeparkingapp.domain.entity.ParkingArea
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingAreasUseCase

class FakeGetParkingAreasUseCase : GetParkingAreasUseCase {

    var shouldReturnError = false
    var errorToReturn: Throwable = Exception("Fake error")

    private val parkingAreasByOwner = mutableMapOf<String, List<ParkingArea>>()

    override suspend fun invoke(ownerId: String): Result<List<ParkingArea>> {
        return if (shouldReturnError) {
            Result.failure(errorToReturn)
        } else {
            parkingAreasByOwner[ownerId]?.let { list ->
                Result.success(list.sortedBy { it.name })
            } ?: Result.success(emptyList())
        }
    }

    fun setParkingAreas(ownerId: String, areas: List<ParkingArea>) {
        parkingAreasByOwner[ownerId] = areas
    }
}