package es.ubu.bikeparkingapp.domain.repository

import es.ubu.bikeparkingapp.domain.entity.Reservation
import es.ubu.bikeparkingapp.domain.entity.ReservationState
import kotlin.time.Instant


/**
 * Representa el repositorio de reservas
 */
interface ReservationRepository {
    // Búsqueda de reservas
    suspend fun findById(reservationId: String): Result<Reservation>
    suspend fun findByAccountId(accountId: String): Result<List<Reservation>>
    suspend fun findByParkingId(parkingAreaId: String): Result<List<Reservation>>
    suspend fun findActiveReservationByAccountId(accountId: String): Result<List<Reservation>>
    suspend fun findActiveReservationByParkingId(parkingAreaId: String): Result<List<Reservation>>
    // Escritura de reservas
    suspend fun save(reservation: Reservation): Result<Unit>
    suspend fun updateState(reservationId: String, newState: ReservationState): Result<Unit>
    suspend fun extend(reservationId: String, newOutTime: Instant): Result<Unit>
    suspend fun countParkingActiveReservations(parkingAreaId: String): Int
    suspend fun countUserActiveReservations(accountId: String): Result<Int>
    suspend fun countCompletedReservationsByUserInParking(accountId: String, parkingAreaId: String): Result<Int>
}