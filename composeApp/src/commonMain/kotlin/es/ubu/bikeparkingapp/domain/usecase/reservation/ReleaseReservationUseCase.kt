package es.ubu.bikeparkingapp.domain.usecase.reservation

/**
 * Interfaz del caso de uso para liberar una reserva.
 */
interface ReleaseReservationUseCase {
    suspend operator fun invoke(reservationId: String): Result<Unit>
}