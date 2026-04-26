package es.ubu.bikeparkingapp.domain.usecase.parking

import es.ubu.bikeparkingapp.domain.entity.ParkingArea

/**
 * Interfaz del caso de uso para obtener los parkings cercanos.
 */
interface GetNearbyParkingAreasUseCase {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        distance: Double): Result<List<ParkingArea>>
}