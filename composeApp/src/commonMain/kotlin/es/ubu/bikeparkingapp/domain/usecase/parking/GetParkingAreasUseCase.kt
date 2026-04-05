package es.ubu.bikeparkingapp.domain.usecase.parking

import es.ubu.bikeparkingapp.domain.entity.ParkingArea

/**
 * Representa la interfaz del caso de uso para obtener los parkings de un propietario.
 *
 */
interface GetParkingAreasUseCase {
    suspend operator fun invoke(ownerId: String): Result<List<ParkingArea>>
}