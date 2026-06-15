package es.ubu.bikeparkingapp.helper.repositories

import es.ubu.bikeparkingapp.domain.entity.Reservation
import es.ubu.bikeparkingapp.domain.entity.ReservationState
import es.ubu.bikeparkingapp.domain.repository.ReservationRepository
import kotlin.time.Instant

class FakeReservationRepository : ReservationRepository {

    val reservations = mutableListOf<Reservation>()
    var shouldReturnError = false
    var errorToReturn: Throwable = Exception("Fake Error")

    private val activeStates = listOf(
        ReservationState.RESERVED,
        ReservationState.CHECKED_IN,
        ReservationState.OVERDUE
    )

    override suspend fun findById(reservationId: String): Result<Reservation> {
        return handleFakeResponse {
            reservations.find { it.reservationId == reservationId }
                ?: throw Exception("Reservation not found")
        }
    }

    override suspend fun findByParkingId(parkingAreaId: String): Result<List<Reservation>> {
        return handleFakeResponse {
            reservations.filter { it.parkingAreaId == parkingAreaId }
        }
    }

    override suspend fun findByAccountId(accountId: String): Result<List<Reservation>> {
        return handleFakeResponse {
            reservations.filter { it.accountId == accountId }
        }
    }

    override suspend fun findActiveReservationByAccountId(accountId: String): Result<List<Reservation>> {
        return handleFakeResponse {
            reservations.filter { it.accountId == accountId && it.state in activeStates }
        }
    }

    override suspend fun findActiveReservationByParkingId(parkingAreaId: String): Result<List<Reservation>> {
        return handleFakeResponse {
            reservations.filter { it.parkingAreaId == parkingAreaId && it.state in activeStates }
        }
    }

    override suspend fun save(reservation: Reservation): Result<Unit> {
        return handleFakeResponse {
            reservations.add(reservation)
            Unit
        }
    }

    override suspend fun updateState(reservationId: String, newState: ReservationState): Result<Unit> {
        return handleFakeResponse {
            val index = reservations.indexOfFirst { it.reservationId == reservationId }
            if (index != -1) {
                reservations[index] = reservations[index].copy(state = newState)
            }
            Unit
        }
    }

    override suspend fun extend(reservationId: String, newOutTime: Instant): Result<Unit> {
        return handleFakeResponse {
            val index = reservations.indexOfFirst { it.reservationId == reservationId }
            if (index != -1) {
                reservations[index] = reservations[index].copy(outTime = newOutTime)
            }
            Unit
        }
    }

    override suspend fun countParkingActiveReservations(parkingAreaId: String): Int {
        return reservations.count { it.parkingAreaId == parkingAreaId && it.state in activeStates }
    }

    override suspend fun countUserActiveReservations(accountId: String): Result<Int> {
        return handleFakeResponse {
            reservations.count { it.accountId == accountId && it.state in activeStates }
        }
    }

    override suspend fun countCompletedReservationsByUserInParking(
        accountId: String,
        parkingAreaId: String
    ): Result<Int> {
        return handleFakeResponse {
            reservations.count {
                it.accountId == accountId &&
                        it.parkingAreaId == parkingAreaId &&
                        it.state == ReservationState.CHECKED_OUT
            }
        }
    }

    private inline fun <T> handleFakeResponse(block: () -> T): Result<T> {
        return if (shouldReturnError) {
            Result.failure(errorToReturn)
        } else {
            runCatching { block() }
        }
    }

    fun clear() {
        reservations.clear()
    }
}