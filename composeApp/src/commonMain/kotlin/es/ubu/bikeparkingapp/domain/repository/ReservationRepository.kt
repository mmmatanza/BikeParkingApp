package es.ubu.bikeparkingapp.domain.repository

import es.ubu.bikeparkingapp.domain.entity.Reservation
import es.ubu.bikeparkingapp.domain.entity.ReservationState


/**
 * Representa el repositorio de reservas
 */
interface ReservationRepository {
    // Búsqueda de reservas
    suspend fun findById(reservationId: String): Result<Reservation>
    suspend fun findByAccountId(accountId: String): Result<List<Reservation>>
    suspend fun findActiveReservationByAccount(accountId: String): Result<List<Reservation>>
    // Escritura de reservas
    suspend fun save(reservation: Reservation): Result<Unit>
    suspend fun updateState(reservationId: String, newState: ReservationState): Result<Unit>
    suspend fun cancelReservation(reservationId: String): Result<Unit>
    suspend fun countActiveReservations(parkingAreaId: String): Int
}