package es.ubu.bikeparkingapp.domain.usecase.occupancy

/**
 * Interfaz para el caso de uso de obtener la ocupación predicha.
 */
interface GetPredictedOccupancyUseCase {
    suspend operator fun invoke(parkingAreaId: String): Result<Int>
}
