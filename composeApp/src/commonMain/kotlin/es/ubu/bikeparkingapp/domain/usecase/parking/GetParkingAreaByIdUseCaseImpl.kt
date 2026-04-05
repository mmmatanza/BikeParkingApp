package es.ubu.bikeparkingapp.domain.usecase.parking

import es.ubu.bikeparkingapp.domain.entity.ParkingArea
import es.ubu.bikeparkingapp.domain.repository.ParkingAreaRepository

class GetParkingAreaByIdUseCaseImpl(
    private val parkingAreaRepository: ParkingAreaRepository
) : GetParkingAreaByIdUseCase {
    override suspend fun invoke(id: String): Result<ParkingArea> {
        return parkingAreaRepository.getParkingAreaById(id)
    }
}