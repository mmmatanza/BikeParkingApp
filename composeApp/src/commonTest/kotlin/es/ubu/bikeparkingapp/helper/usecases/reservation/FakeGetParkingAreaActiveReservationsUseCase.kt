package es.ubu.bikeparkingapp.helper.usecases.reservation

import es.ubu.bikeparkingapp.domain.entity.Reservation
import es.ubu.bikeparkingapp.domain.usecase.reservation.GetParkingAreaActiveReservationsUseCase

class FakeGetParkingAreaActiveReservationsUseCase : GetParkingAreaActiveReservationsUseCase {

    var receivedParkingAreaId: String? = null
    var response: List<Reservation> = emptyList()

    var shouldFail = false
    var exception: Throwable = Exception("Fake error")

    override suspend operator fun invoke(
        parkingAreaId: String
    ): Result<List<Reservation>> {

        receivedParkingAreaId = parkingAreaId

        return if (shouldFail) {
            Result.failure(exception)
        } else {
            Result.success(response)
        }
    }

}