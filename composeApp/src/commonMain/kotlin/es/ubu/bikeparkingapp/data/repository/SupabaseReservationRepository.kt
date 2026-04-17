package es.ubu.bikeparkingapp.data.repository

import es.ubu.bikeparkingapp.data.dto.ReservationDto
import es.ubu.bikeparkingapp.data.mapper.toDomain
import es.ubu.bikeparkingapp.data.mapper.toDto
import es.ubu.bikeparkingapp.domain.entity.Reservation
import es.ubu.bikeparkingapp.domain.entity.ReservationState
import es.ubu.bikeparkingapp.domain.repository.ReservationRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

/**
 * Representa el repositorio de reservas en Supabase.
 */
class SupabaseReservationRepository(
    private val client: SupabaseClient
) : ReservationRepository {
    override suspend fun findById(reservationId: String): Result<Reservation> {
        return runCatching {
            client.from("reservations")
                .select {
                    filter {
                        eq("reservation_id", reservationId)
                    }
                }
                .decodeSingle<ReservationDto>().toDomain()
        }
    }

    override suspend fun findByParkingId(parkingAreaId: String): Result<List<Reservation>> {
        return runCatching {
            client.from("reservations")
                .select {
                    filter {
                        eq("parking_area_id", parkingAreaId)
                    }
                }
                .decodeList<ReservationDto>().map { it.toDomain() }
        }
    }

    override suspend fun findByAccountId(accountId: String): Result<List<Reservation>> {
        return runCatching {
            client.from("reservations")
                .select {
                    filter {
                        eq("account_id", accountId)
                    }
                }.decodeList<ReservationDto>().map { it.toDomain() }
        }
    }

    override suspend fun findActiveReservationByAccountId(accountId: String): Result<List<Reservation>> {
        return runCatching {
            client.from("reservations")
                .select {
                    filter {
                        eq("account_id", accountId)
                        isIn("state", listOf(
                            ReservationState.RESERVED.toString(),
                            ReservationState.CHECKED_IN.toString()
                        ))
                    }
                }.decodeList<ReservationDto>().map { it.toDomain() }
        }
    }

    override suspend fun findActiveReservationByParkingId(parkingAreaId: String): Result<List<Reservation>> {
        return runCatching {
            client.from("reservations")
                .select {
                    filter {
                        eq("parking_area_id", parkingAreaId)
                        isIn("state", listOf(
                            ReservationState.RESERVED.toString(),
                            ReservationState.CHECKED_IN.toString()
                        ))
                    }
                }.decodeList<ReservationDto>().map { it.toDomain() }
        }
    }

    override suspend fun save(reservation: Reservation): Result<Unit> {
        return runCatching {
            client.from("reservations")
                .insert(reservation.toDto())
        }
    }

    override suspend fun updateState(reservationId: String, newState: ReservationState): Result<Unit> {
        return runCatching {
            client.from("reservations")
                .update(mapOf("state" to newState.name)) {
                    filter { eq("reservation_id", reservationId) }
                }
        }
    }

    override suspend fun countParkingActiveReservations(parkingAreaId: String): Int {
        return client.from("reservations")
            .select {
                filter {
                    eq("parking_area_id", parkingAreaId)
                    isIn("state", listOf(
                        ReservationState.RESERVED.toString(),
                        ReservationState.CHECKED_IN.toString(),
                        ReservationState.OVERDUE.toString()
                    ))
                }
            }
            .decodeList<ReservationDto>()
            .size
    }

    override suspend fun countUserActiveReservations(accountId: String): Int {
        return client.from("reservations")
            .select {
                filter {
                    eq("account_id", accountId)
                    isIn("state", listOf(
                        ReservationState.RESERVED.toString(),
                        ReservationState.CHECKED_IN.toString(),
                        ReservationState.OVERDUE.toString()
                    ))
                }
            }
            .decodeList<ReservationDto>()
            .size
    }
}