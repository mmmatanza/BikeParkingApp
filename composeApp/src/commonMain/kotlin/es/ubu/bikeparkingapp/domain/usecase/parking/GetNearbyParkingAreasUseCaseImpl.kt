package es.ubu.bikeparkingapp.domain.usecase.parking

import es.ubu.bikeparkingapp.domain.entity.ParkingArea
import es.ubu.bikeparkingapp.domain.repository.ParkingAreaRepository

/**
 * Implementación del caso de uso para obtener los parkings cercanos.
 * @property parkingAreaRepository Repositorio de parkings.
 * @return Los parkings cercanos.
 */
class GetNearbyParkingAreasUseCaseImpl(
    private val parkingAreaRepository: ParkingAreaRepository
) : GetNearbyParkingAreasUseCase {
    override suspend fun invoke(
        latitude: Double,
        longitude: Double,
        distance: Double
    ): Result<List<ParkingArea>> {
        return parkingAreaRepository.getNearbyParkingAreas(latitude, longitude, distance)
    }
}