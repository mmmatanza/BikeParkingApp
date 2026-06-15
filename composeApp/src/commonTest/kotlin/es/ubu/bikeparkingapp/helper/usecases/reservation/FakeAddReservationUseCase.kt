package es.ubu.bikeparkingapp.helper.usecases.reservation

import es.ubu.bikeparkingapp.domain.usecase.reservation.AddReservationUseCase

class FakeAddReservationUseCase : AddReservationUseCase {

    var receivedParkingAreaId: String? = null
    var receivedAccountId: String? = null

    var shouldFail = false
    var errorToReturn: Throwable = Exception("Fake error")

    var receivedDistance: Double? = null

    override suspend operator fun invoke(
        parkingAreaId: String,
        accountId: String,
        distance: Double?
    ): Result<Unit> {

        receivedParkingAreaId = parkingAreaId
        receivedAccountId = accountId
        receivedDistance = distance

        return if (shouldFail) {
            Result.failure(errorToReturn)
        } else {
            Result.success(Unit)
        }
    }
}