package es.ubu.bikeparkingapp.domain.usecase.parking

import es.ubu.bikeparkingapp.domain.entity.ParkingArea
import es.ubu.bikeparkingapp.domain.repository.ParkingAreaRepository

/**
 * Implementación del caso de uso para obtener un parking por su id.
 * @property parkingAreaRepository Repositorio de parkings.
 * @return El parking con el id especificado.
 */
class GetParkingAreaByIdUseCaseImpl(
    private val parkingAreaRepository: ParkingAreaRepository
) : GetParkingAreaByIdUseCase {
    override suspend fun invoke(parkingAreaId: String): Result<ParkingArea> {
        return parkingAreaRepository.getParkingAreaById(parkingAreaId)
    }
}
