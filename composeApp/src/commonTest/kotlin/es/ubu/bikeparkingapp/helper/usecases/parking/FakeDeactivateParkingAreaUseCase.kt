package es.ubu.bikeparkingapp.helper.usecases.parking

import es.ubu.bikeparkingapp.domain.usecase.parking.DeactivateParkingAreaUseCase

class FakeDeactivateParkingAreaUseCase : DeactivateParkingAreaUseCase {

    var shouldReturnError = false
    var errorToReturn: Throwable = Exception("Fake error")

    private val deactivatedIds = mutableListOf<String>()

    override suspend fun invoke(parkingAreaId: String): Result<Unit> {
        return if (shouldReturnError) {
            Result.failure(errorToReturn)
        } else {
            deactivatedIds.add(parkingAreaId)
            Result.success(Unit)
        }
    }

    fun wasIdDeactivated(parkingAreaId: String): Boolean {
        return deactivatedIds.contains(parkingAreaId)
    }

    fun getDeactivatedCount(): Int = deactivatedIds.size
}