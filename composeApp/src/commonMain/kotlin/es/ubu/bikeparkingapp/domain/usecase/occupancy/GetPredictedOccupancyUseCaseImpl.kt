package es.ubu.bikeparkingapp.domain.usecase.occupancy

import es.ubu.bikeparkingapp.domain.repository.OccupancyRepository

/**
 * Implementación del caso de uso para obtener la ocupación predicha.
 * @property occupancyRepository Repositorio de ocupación.
 */
class GetPredictedOccupancyUseCaseImpl(
    private val occupancyRepository: OccupancyRepository
) : GetPredictedOccupancyUseCase {
    override suspend fun invoke(parkingAreaId: String): Result<Int> {
        return occupancyRepository.getPredictedOccupancy(parkingAreaId)
    }
}
