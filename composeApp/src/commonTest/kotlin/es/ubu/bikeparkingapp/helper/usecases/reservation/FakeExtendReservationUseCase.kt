package es.ubu.bikeparkingapp.helper.usecases.reservation

import es.ubu.bikeparkingapp.domain.usecase.reservation.ExtendReservationUseCase
import kotlin.time.Instant

class FakeExtendReservationUseCase : ExtendReservationUseCase {

    var receivedReservationId: String? = null
    var receivedCurrentOutTime: Instant? = null
    var receivedMinutes: Int? = null

    var shouldFail = false
    var exception: Throwable = Exception("Fake error")

    override suspend operator fun invoke(
        reservationId: String,
        currentOutTime: Instant,
        minutes: Int
    ): Result<Unit> {

        receivedReservationId = reservationId
        receivedCurrentOutTime = currentOutTime
        receivedMinutes = minutes

        return if (shouldFail) {
            Result.failure(exception)
        } else {
            Result.success(Unit)
        }
    }

}