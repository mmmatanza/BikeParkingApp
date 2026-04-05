package es.ubu.bikeparkingapp.domain.usecase.parking

/**
 * Representa la interfaz del caso de uso para desactivar un parking.
 *
 */
interface DeactivateParkingAreaUseCase {
    suspend operator fun invoke(parkingId: String): Result<Unit>
}