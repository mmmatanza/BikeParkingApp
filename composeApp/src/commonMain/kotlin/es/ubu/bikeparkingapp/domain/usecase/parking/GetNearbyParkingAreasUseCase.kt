package es.ubu.bikeparkingapp.domain.usecase.parking

import es.ubu.bikeparkingapp.domain.entity.ParkingArea

interface GetNearbyParkingAreasUseCase {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        distance: Double): Result<List<ParkingArea>>
}