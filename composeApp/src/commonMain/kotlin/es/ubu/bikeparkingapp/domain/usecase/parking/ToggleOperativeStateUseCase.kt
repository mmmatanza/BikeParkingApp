package es.ubu.bikeparkingapp.domain.usecase.parking

/**
 * Interfaz del caso de uso para cambiar el estado operativo de un parking.
 */
interface ToggleOperativeStateUseCase {
    suspend operator fun invoke(parkingAreaId: String, isOperative: Boolean): Result<Unit>
}