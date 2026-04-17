package es.ubu.bikeparkingapp.domain.usecase.reservation

/**
 * Interfaz del caso de uso para hacer check-in en una reserva.
 */
interface CheckInReservationUseCase {
    suspend operator fun invoke(reservationId: String): Result<Unit>
}