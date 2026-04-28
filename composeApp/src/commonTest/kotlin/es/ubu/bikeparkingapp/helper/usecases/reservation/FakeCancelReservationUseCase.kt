package es.ubu.bikeparkingapp.helper.usecases.reservation

import es.ubu.bikeparkingapp.domain.usecase.reservation.CancelReservationUseCase

class FakeCancelReservationUseCase : CancelReservationUseCase {

    var receivedReservationId: String? = null

    var shouldFail = false
    var exception: Throwable = Exception("Fake error")

    override suspend operator fun invoke(
        reservationId: String
    ): Result<Unit> {

        receivedReservationId = reservationId

        return if (shouldFail) {
            Result.failure(exception)
        } else {
            Result.success(Unit)
        }
    }

}