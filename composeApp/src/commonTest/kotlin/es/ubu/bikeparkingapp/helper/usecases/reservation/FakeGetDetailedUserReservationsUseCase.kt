package es.ubu.bikeparkingapp.helper.usecases.reservation

import es.ubu.bikeparkingapp.domain.entity.ReservationDetail
import es.ubu.bikeparkingapp.domain.usecase.reservation.GetDetailedUserReservationsUseCase

class FakeGetDetailedUserReservationsUseCase : GetDetailedUserReservationsUseCase {

    var receivedAccountId: String? = null
    var response: List<ReservationDetail> = emptyList()

    var shouldFail = false
    var exception: Throwable = Exception("Fake error")

    override suspend operator fun invoke(accountId: String): Result<List<ReservationDetail>> {

        receivedAccountId = accountId

        return if (shouldFail) {
            Result.failure(exception)
        } else {
            Result.success(response)
        }
    }

}