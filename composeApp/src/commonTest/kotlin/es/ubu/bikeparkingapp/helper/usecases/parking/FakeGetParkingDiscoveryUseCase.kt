package es.ubu.bikeparkingapp.helper.usecases.parking

import es.ubu.bikeparkingapp.domain.entity.ParkingDiscovery
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingDiscoveryUseCase

class FakeGetParkingDiscoveryUseCase : GetParkingDiscoveryUseCase {

    var shouldReturnError = false
    var errorToReturn: Throwable = Exception("Fake error")

    var response: ParkingDiscovery? = null

    private var lastCapturedParams: Triple<Double, Double, Double>? = null

    override suspend fun invoke(
        latitude: Double,
        longitude: Double,
        distance: Double
    ): Result<ParkingDiscovery> {
        return if (shouldReturnError) {
            Result.failure(errorToReturn)
        } else {
            lastCapturedParams = Triple(latitude, longitude, distance)
            Result.success(response ?: ParkingDiscovery(null, emptyList()))
        }
    }

    fun getLastParams() = lastCapturedParams
}
