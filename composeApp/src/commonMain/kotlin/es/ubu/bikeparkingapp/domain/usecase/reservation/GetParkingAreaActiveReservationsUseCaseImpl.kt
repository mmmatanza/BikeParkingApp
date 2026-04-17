package es.ubu.bikeparkingapp.domain.usecase.reservation

import es.ubu.bikeparkingapp.domain.entity.Reservation
import es.ubu.bikeparkingapp.domain.repository.ReservationRepository

/**
 * Implementación del caso de uso para obtener las reservas activas de un parking.
 */
class GetParkingAreaActiveReservationsUseCaseImpl(
    private val reservationRepository: ReservationRepository
): GetParkingAreaActiveReservationsUseCase {
    override suspend fun invoke(parkingAreaId: String): Result<List<Reservation>> = runCatching {
        return reservationRepository.findActiveReservationByParkingId(parkingAreaId)
    }
}