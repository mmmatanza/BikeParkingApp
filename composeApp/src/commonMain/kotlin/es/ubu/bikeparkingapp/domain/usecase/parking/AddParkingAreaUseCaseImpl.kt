package es.ubu.bikeparkingapp.domain.usecase.parking

import es.ubu.bikeparkingapp.domain.entity.ParkingArea
import es.ubu.bikeparkingapp.domain.repository.ParkingAreaRepository
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone

/**
 * Representa el caso de uso para añadir un parking.
 * @property parkingAreaRepository Repositorio de parkings
 */
class AddParkingAreaUseCaseImpl(
    private val parkingAreaRepository: ParkingAreaRepository
) : AddParkingAreaUseCase {
    override suspend operator fun invoke(
        ownerId: String,
        name: String,
        address: String,
        capacity: Int,
        openingTime: String,
        closingTime: String,
        latitude: Double,
        longitude: Double,
        rules: List<String>,
        openDays: Set<DayOfWeek>
    ): Result<Unit> {
        val parkingArea = ParkingArea(
            parkingAreaId = null,
            ownerId = ownerId,
            name = name,
            address = address,
            latitude = latitude,
            longitude = longitude,
            capacity = capacity,
            currentOccupancy = 0,
            isOperative = true,
            isActive = true,
            timezoneId = TimeZone.currentSystemDefault().id,
            openingTime = openingTime,
            closingTime = closingTime,
            rules = rules,
            openDays = openDays
        )
        return parkingAreaRepository.addParkingArea(parkingArea)
    }
}