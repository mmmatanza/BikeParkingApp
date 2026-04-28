package es.ubu.bikeparkingapp.helper.usecases.reservation

import es.ubu.bikeparkingapp.domain.entity.Reservation
import es.ubu.bikeparkingapp.domain.usecase.reservation.GetUserReservationsUseCase

class FakeGetUserReservationsUseCase : GetUserReservationsUseCase {

    var receivedAccountId: String? = null
    var response: List<Reservation> = emptyList()

    var shouldFail = false
    var exception: Throwable = Exception("Fake error")

    override suspend operator fun invoke(accountId: String): Result<List<Reservation>> {

        receivedAccountId = accountId

        return if (shouldFail) {
            Result.failure(exception)
        } else {
            Result.success(response)
        }
    }

}