package es.ubu.bikeparkingapp.helper.usecases.parking

import es.ubu.bikeparkingapp.domain.usecase.parking.AddParkingAreaUseCase
import kotlinx.datetime.DayOfWeek

class FakeAddParkingAreaUseCase : AddParkingAreaUseCase {

    var shouldReturnError = false
    var errorToReturn: Throwable = Exception("Fake error")

    private val addedCalls = mutableListOf<String>()

    override suspend fun invoke(
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
        return if (shouldReturnError) {
            Result.failure(errorToReturn)
        } else {
            addedCalls.add(name)
            Result.success(Unit)
        }
    }

    fun wasCalledWithName(name: String): Boolean {
        return addedCalls.contains(name)
    }

}