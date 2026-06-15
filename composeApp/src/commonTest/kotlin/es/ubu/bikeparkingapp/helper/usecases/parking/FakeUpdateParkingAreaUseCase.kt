package es.ubu.bikeparkingapp.helper.usecases.parking

import es.ubu.bikeparkingapp.domain.entity.ParkingArea
import es.ubu.bikeparkingapp.domain.usecase.parking.UpdateParkingAreaUseCase
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone

class FakeUpdateParkingAreaUseCase : UpdateParkingAreaUseCase {

    var receivedParkingArea: ParkingArea? = null

    var shouldFail = false
    var errorToReturn: Throwable = Exception("Fake error")

    override suspend operator fun invoke(
        parkingAreaId: String,
        ownerId: String,
        name: String,
        address: String,
        capacity: Int,
        openingTime: String,
        closingTime: String,
        latitude: Double,
        longitude: Double,
        rules: List<String>,
        openDays: Set<DayOfWeek>,
        occupancyThreshold: Int?
    ): Result<Unit> {

        receivedParkingArea = ParkingArea(
            parkingAreaId = parkingAreaId,
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
            openDays = openDays,
            rules = rules,
            occupancyThreshold = occupancyThreshold
        )

        return if (shouldFail) {
            Result.failure(errorToReturn)
        } else {
            Result.success(Unit)
        }
    }
}