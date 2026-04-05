package es.ubu.bikeparkingapp.domain.usecase.parking

import es.ubu.bikeparkingapp.domain.entity.ParkingArea
import es.ubu.bikeparkingapp.domain.repository.ParkingAreaRepository

/**
 * Representa el caso de uso para añadir un parking.
 *
 * @property parkingAreaRepository Repositorio de parkings
 */
class AddParkingAreaUseCaseImpl(
    private val parkingAreaRepository: ParkingAreaRepository
) : AddParkingAreaUseCase {
    override suspend operator fun invoke(
        ownerId: String,
        name: String,
        capacity: Int,
        openingTime: String,
        closingTime: String,
        latitude: Double,
        longitude: Double,
        rules: List<String>
    ): Result<Unit> {
        val parkingArea = ParkingArea(
            id = null,
            ownerId = ownerId,
            name = name,
            latitude = latitude,
            longitude = longitude,
            capacity = capacity,
            currentOccupancy = 0,
            isOperative = true,
            isActive = true,
            openingTime = openingTime,
            closingTime = closingTime,
            rules = rules
        )
        return parkingAreaRepository.addParkingArea(parkingArea)
    }
}