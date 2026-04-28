package es.ubu.bikeparkingapp.helper.usecases.parking

import es.ubu.bikeparkingapp.domain.entity.ParkingArea
import es.ubu.bikeparkingapp.domain.usecase.parking.GetNearbyParkingAreasUseCase

class FakeGetNearbyParkingAreasUseCase : GetNearbyParkingAreasUseCase {

    var shouldReturnError = false
    var errorToReturn: Throwable = Exception("Fake error")

    var parkingAreasToReturn = mutableListOf<ParkingArea>()

    private var lastCapturedParams: Triple<Double, Double, Double>? = null

    override suspend fun invoke(
        latitude: Double,
        longitude: Double,
        distance: Double
    ): Result<List<ParkingArea>> {
        return if (shouldReturnError) {
            Result.failure(errorToReturn)
        } else {
            lastCapturedParams = Triple(latitude, longitude, distance)
            Result.success(parkingAreasToReturn)
        }
    }

    fun getLastParams() = lastCapturedParams
}