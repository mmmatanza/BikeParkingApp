package es.ubu.bikeparkingapp.domain.usecase.reservation

import es.ubu.bikeparkingapp.domain.entity.ReservationDetail
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingAreaByIdUseCase

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
    override suspend operator fun invoke(accountId: String): Result<List<ReservationDetail>> {
        return getUserReservationsUseCase(accountId).map { reservations ->
            reservations.map { reservation ->
                // Buscamos el parking asociado a cada reserva
                val parkingResult = getParkingAreaByIdUseCase(reservation.parkingAreaId)

                ReservationDetail(
                    reservation = reservation,
                    parkingName = parkingResult.getOrNull()?.name ?: "",
                    parkingAddress = parkingResult.getOrNull()?.address ?: "",
                    parkingLatitude = parkingResult.getOrNull()?.latitude ?: 0.0,
                    parkingLongitude = parkingResult.getOrNull()?.longitude ?: 0.0
                )
            }
        }
    }
}
