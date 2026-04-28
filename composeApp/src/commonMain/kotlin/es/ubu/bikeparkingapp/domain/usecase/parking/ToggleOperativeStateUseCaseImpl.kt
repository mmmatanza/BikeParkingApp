package es.ubu.bikeparkingapp.domain.usecase.parking

import es.ubu.bikeparkingapp.domain.repository.ParkingAreaRepository

/**
 * Caso de uso para cambiar el estado operativo de un parking.
 * @property parkingAreaRepository Repositorio de parkings.
 */
class ToggleOperativeStateUseCaseImpl(
    val parkingAreaRepository: ParkingAreaRepository
) : ToggleOperativeStateUseCase {
    override suspend fun invoke(parkingAreaId: String, isOperative: Boolean): Result<Unit> {
        return parkingAreaRepository.toggleOperativeState(parkingAreaId, isOperative)
    }
}