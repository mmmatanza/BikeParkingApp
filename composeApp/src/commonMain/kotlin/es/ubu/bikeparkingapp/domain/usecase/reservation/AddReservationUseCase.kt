package es.ubu.bikeparkingapp.domain.usecase.reservation

/**
 * Interfaz del caso de uso para añadir una reserva.
 */
interface AddReservationUseCase {
    suspend operator fun invoke(
        parkingAreaId: String,
        accountId: String,
        distance: Double? = null
    ): Result<Unit>
}