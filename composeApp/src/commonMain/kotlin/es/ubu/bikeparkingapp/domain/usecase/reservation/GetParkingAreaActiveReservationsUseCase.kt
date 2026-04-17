package es.ubu.bikeparkingapp.domain.usecase.reservation

import es.ubu.bikeparkingapp.domain.entity.Reservation

/**
 * Interfaz del caso de uso para obtener las reservas activas de un parking.
 */
interface GetParkingAreaActiveReservationsUseCase {
    suspend operator fun invoke(parkingAreaId: String): Result<List<Reservation>>
}