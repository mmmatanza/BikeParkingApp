package es.ubu.bikeparkingapp.helper.usecases.reservation

import es.ubu.bikeparkingapp.domain.usecase.reservation.AddReservationUseCase

class FakeAddReservationUseCase : AddReservationUseCase {

    var receivedParkingAreaId: String? = null
    var receivedAccountId: String? = null

    var shouldFail = false
    var errorToReturn: Throwable = Exception("Fake error")

    override suspend operator fun invoke(
        parkingAreaId: String,
        accountId: String
    ): Result<Unit> {

        receivedParkingAreaId = parkingAreaId
        receivedAccountId = accountId

        return if (shouldFail) {
            Result.failure(errorToReturn)
        } else {
            Result.success(Unit)
        }
    }
}