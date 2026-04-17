package es.ubu.bikeparkingapp.domain.usecase.reservation

/**
 * Interfaz del caso de uso para hacer check-out en una reserva.
 */
interface CheckOutReservationUseCase {
    suspend operator fun invoke(reservationId: String): Result<Unit>
}