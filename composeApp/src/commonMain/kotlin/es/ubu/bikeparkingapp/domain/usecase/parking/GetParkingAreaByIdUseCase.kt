package es.ubu.bikeparkingapp.domain.usecase.parking

import es.ubu.bikeparkingapp.domain.entity.ParkingArea

/**
 * Interfaz del caso de uso para obtener un parking por su id.
 */
interface GetParkingAreaByIdUseCase {
    suspend operator fun invoke(id: String): Result<ParkingArea>
}