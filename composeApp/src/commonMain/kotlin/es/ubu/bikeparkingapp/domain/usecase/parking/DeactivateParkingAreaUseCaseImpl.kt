package es.ubu.bikeparkingapp.domain.usecase.parking

import es.ubu.bikeparkingapp.domain.repository.ParkingAreaRepository

/**
 * Representa el caso de uso para desactivar un parking.
 * @property parkingAreaRepository Repositorio de parkings
 *
 */
class DeactivateParkingAreaUseCaseImpl(
    private val parkingAreaRepository: ParkingAreaRepository
): DeactivateParkingAreaUseCase {
    override suspend fun invoke(parkingId: String): Result<Unit> {
        return parkingAreaRepository.deactivateParkingArea(parkingId)
    }
}