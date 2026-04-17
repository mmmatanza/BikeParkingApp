package es.ubu.bikeparkingapp.domain.usecase.reservation

import es.ubu.bikeparkingapp.domain.entity.Reservation

/**
 * Interfaz del caso de uso para obtener las reservas de un usuario.
 */
interface GetUserReservationsUseCase {
    suspend operator fun invoke(accountId: String): Result<List<Reservation>>
}