package es.ubu.bikeparkingapp.domain.usecase.reservation

import es.ubu.bikeparkingapp.domain.entity.ReservationDetail

/**
 * Interfaz que define el caso de uso para obtener las reservas del usuario detalladas.
 */
interface GetDetailedUserReservationsUseCase {
    suspend operator fun invoke(accountId: String): Result<List<ReservationDetail>>
}