package es.ubu.bikeparkingapp.data.repository

import es.ubu.bikeparkingapp.data.dto.ReservationDto
import es.ubu.bikeparkingapp.data.mapper.ErrorMapper
import es.ubu.bikeparkingapp.data.mapper.toDomain
import es.ubu.bikeparkingapp.data.mapper.toDto
import es.ubu.bikeparkingapp.domain.entity.Reservation
import es.ubu.bikeparkingapp.domain.entity.ReservationState
import es.ubu.bikeparkingapp.domain.repository.ReservationRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlin.time.Instant

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
        }.recoverCatching { throwable ->
            // Mapeamos la excepción de SQL a domain
            throw ErrorMapper.map(throwable)
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
        }.recoverCatching { throwable ->
            // Mapeamos la excepción de SQL a domain
            throw ErrorMapper.map(throwable)
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
        }.recoverCatching { throwable ->
            // Mapeamos la excepción de SQL a domain
            throw ErrorMapper.map(throwable)
        }
    }

    override suspend fun findActiveReservationByAccountId(accountId: String): Result<List<Reservation>> {
        return runCatching {
            client.from("reservations")
                .select {
                    filter {
                        eq("account_id", accountId)
                        isIn(
                            "state", listOf(
                                ReservationState.RESERVED.toString(),
                                ReservationState.CHECKED_IN.toString(),
                                ReservationState.OVERDUE.toString()
                            )
                        )
                    }
                }.decodeList<ReservationDto>().map { it.toDomain() }
        }.recoverCatching { throwable ->
            // Mapeamos la excepción de SQL a domain
            throw ErrorMapper.map(throwable)
        }
    }

    override suspend fun findActiveReservationByParkingId(parkingAreaId: String): Result<List<Reservation>> {
        return runCatching {
            client.from("reservations")
                .select {
                    filter {
                        eq("parking_area_id", parkingAreaId)
                        isIn(
                            "state", listOf(
                                ReservationState.RESERVED.toString(),
                                ReservationState.CHECKED_IN.toString(),
                                ReservationState.OVERDUE.toString()
                            )
                        )
                    }
                }.decodeList<ReservationDto>().map { it.toDomain() }
        }.recoverCatching { throwable ->
            // Mapeamos la excepción de SQL a domain
            throw ErrorMapper.map(throwable)
        }
    }

    override suspend fun save(reservation: Reservation): Result<Unit> {
        return runCatching {
            client.from("reservations")
                .insert(reservation.toDto())
            Unit
        }.recoverCatching { throwable ->
            // Mapeamos la excepción de SQL a domain
            throw ErrorMapper.map(throwable)
        }
    }

    override suspend fun updateState(
        reservationId: String,
        newState: ReservationState
    ): Result<Unit> {
        return runCatching {
            client.from("reservations")
                .update(mapOf("state" to newState.name)) {
                    filter { eq("reservation_id", reservationId) }
                }
            Unit
        }.recoverCatching { throwable ->
            // Mapeamos la excepción de SQL a domain
            throw ErrorMapper.map(throwable)
        }
    }

    override suspend fun extend(
        reservationId: String,
        newOutTime: Instant
    ): Result<Unit> = runCatching {
        // Ejecutamos la operación
        client.from("reservations")
            .update(mapOf("out_time" to newOutTime.toString())) {
                filter { eq("reservation_id", reservationId) }
            }
        // Si llegamos aquí sin excepciones, forzamos el retorno de Unit
        Unit
    }.recoverCatching { throwable ->
        // Mapeamos la excepción de SQL a domain
        throw ErrorMapper.map(throwable)
    }

    override suspend fun countParkingActiveReservations(parkingAreaId: String): Int {
        return client.from("reservations")
            .select {
                filter {
                    eq("parking_area_id", parkingAreaId)
                    isIn(
                        "state", listOf(
                            ReservationState.RESERVED.toString(),
                            ReservationState.CHECKED_IN.toString(),
                            ReservationState.OVERDUE.toString()
                        )
                    )
                }
            }
            .decodeList<ReservationDto>()
            .size
    }

    override suspend fun countUserActiveReservations(accountId: String): Result<Int> {
        return runCatching {
            client.from("reservations")
                .select {
                    filter {
                        eq("account_id", accountId)
                        isIn(
                            "state", listOf(
                                ReservationState.RESERVED.toString(),
                                ReservationState.CHECKED_IN.toString(),
                                ReservationState.OVERDUE.toString()
                            )
                        )
                    }
                }
                .decodeList<ReservationDto>()
                .size
        }.recoverCatching { throwable ->
            // Mapeamos la excepción de SQL a domain
            throw ErrorMapper.map(throwable)
        }
    }
}