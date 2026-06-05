package es.ubu.bikeparkingapp.domain.usecase.occupancy

import es.ubu.bikeparkingapp.domain.entity.OccupancyPrediction

/**
 * Interfaz para el caso de uso de obtener la ocupación predicha.
 */
interface GetPredictedOccupancyUseCase {
    suspend operator fun invoke(parkingAreaId: String): Result<OccupancyPrediction>
}
