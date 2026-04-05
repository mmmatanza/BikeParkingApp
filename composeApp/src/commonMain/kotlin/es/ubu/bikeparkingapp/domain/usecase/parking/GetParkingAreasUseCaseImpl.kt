package es.ubu.bikeparkingapp.domain.usecase.parking

import es.ubu.bikeparkingapp.domain.entity.ParkingArea
import es.ubu.bikeparkingapp.domain.repository.ParkingAreaRepository

/**
 * Representa el caso de uso para obtener los parkings de un propietario.
 *
 * @property repository Repositorio de parkings
 */
class GetParkingAreasUseCaseImpl(
    private val parkingAreaRepository: ParkingAreaRepository
) : GetParkingAreasUseCase {

    override suspend fun invoke(ownerId: String): Result<List<ParkingArea>> {
        return parkingAreaRepository.getParkingAreasByOwner(ownerId).map { list ->
            list.sortedBy { it.name }
        }
    }
}