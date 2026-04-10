package es.ubu.bikeparkingapp.domain.usecase.parking

import es.ubu.bikeparkingapp.domain.entity.ParkingArea
import es.ubu.bikeparkingapp.domain.repository.ParkingAreaRepository

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