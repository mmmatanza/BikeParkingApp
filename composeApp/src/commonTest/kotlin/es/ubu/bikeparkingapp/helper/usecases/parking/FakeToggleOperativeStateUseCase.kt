package es.ubu.bikeparkingapp.helper.usecases.parking

import es.ubu.bikeparkingapp.domain.usecase.parking.ToggleOperativeStateUseCase

class FakeToggleOperativeStateUseCase : ToggleOperativeStateUseCase {
    var lastParkingAreaId: String? = null
    var lastIsOperative: Boolean? = null

    var shouldReturnError = false
    var errorToReturn: Throwable = Exception("Fake error")

    override suspend fun invoke(
        parkingAreaId: String,
        isOperative: Boolean
    ): Result<Unit> {

        lastParkingAreaId = parkingAreaId
        lastIsOperative = isOperative

        return if (shouldReturnError) {
            Result.failure(errorToReturn)
        } else {
            Result.success(Unit)
        }
    }
}