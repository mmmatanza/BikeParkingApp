package es.ubu.bikeparkingapp.domain.usecase.reservation

/**
 * Interfaz del caso de uso para cancelar una reserva.
 */
interface CancelReservationUseCase {
    suspend operator fun invoke(
        reservationId: String
    ): Result<Unit>
}