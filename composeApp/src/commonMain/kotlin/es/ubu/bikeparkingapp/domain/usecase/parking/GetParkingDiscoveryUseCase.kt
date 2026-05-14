package es.ubu.bikeparkingapp.domain.usecase.parking

import es.ubu.bikeparkingapp.domain.entity.ParkingDiscovery

/**
 * Interfaz del caso de uso para obtener los parkings cercanos y el recomendado.
 */
interface GetParkingDiscoveryUseCase {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        distance: Double
    ): Result<ParkingDiscovery>
}
