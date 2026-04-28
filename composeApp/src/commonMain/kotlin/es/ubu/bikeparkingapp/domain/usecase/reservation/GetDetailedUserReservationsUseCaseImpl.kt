package es.ubu.bikeparkingapp.domain.usecase.reservation

import es.ubu.bikeparkingapp.domain.entity.ReservationDetail
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingAreaByIdUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Define el caso de uso para obtener las reservas del usuario detalladas.
 * @param getUserReservationsUseCase Caso de uso para obtener las reservas del usuario.
 * @param getParkingAreaByIdUseCase Caso de uso para obtener el parking por su id.
 * @return La lista de reservas detalladas del usuario.
 */

class GetDetailedUserReservationsUseCaseImpl(
    private val getUserReservationsUseCase: GetUserReservationsUseCase,
    private val getParkingAreaByIdUseCase: GetParkingAreaByIdUseCase
) : GetDetailedUserReservationsUseCase {
    override suspend operator fun invoke(accountId: String): Result<List<ReservationDetail>> = coroutineScope {
        getUserReservationsUseCase(accountId).map { reservations ->
            reservations.map { reservation ->
                async {
                    val parking = getParkingAreaByIdUseCase(reservation.parkingAreaId).getOrNull()

                    ReservationDetail(
                        reservation = reservation,
                        parkingName = parking?.name ?: "",
                        parkingAddress = parking?.address ?: "",
                        parkingLatitude = parking?.latitude ?: 0.0,
                        parkingLongitude = parking?.longitude ?: 0.0
                    )
                }
            }.awaitAll()
        }
    }
}
