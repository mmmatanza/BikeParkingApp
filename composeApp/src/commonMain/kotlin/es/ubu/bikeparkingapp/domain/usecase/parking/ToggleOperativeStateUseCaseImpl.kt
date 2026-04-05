package es.ubu.bikeparkingapp.domain.usecase.parking

import es.ubu.bikeparkingapp.domain.repository.ParkingAreaRepository

class ToggleOperativeStateUseCaseImpl(
    val parkingAreaRepository: ParkingAreaRepository
) : ToggleOperativeStateUseCase {
    override suspend fun invoke(parkingId: String, isOperative: Boolean): Result<Unit> {
        return runCatching {
            parkingAreaRepository.toggleOperativeState(parkingId, isOperative)
        }
    }
}