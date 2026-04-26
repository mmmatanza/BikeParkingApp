package es.ubu.bikeparkingapp.domain.usecase.reservation

import kotlin.time.Instant

/**
 * Interfaz que define el caso de uso para extender una reserva.
 */
interface ExtendReservationUseCase {
    suspend operator fun invoke(
        reservationId: String,
        currentOutTime: Instant,
        minutes: Int
    ): Result<Unit>
}
