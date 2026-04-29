package es.ubu.bikeparkingapp.helper.usecases.parking

import es.ubu.bikeparkingapp.domain.entity.ParkingArea
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingAreaByIdUseCase

class FakeGetParkingAreaByIdUseCase: GetParkingAreaByIdUseCase {

    var shouldReturnError = false
    var errorToReturn: Throwable = Exception("Fake error")

    var response: ParkingArea? = null


    override suspend fun invoke(parkingAreaId: String): Result<ParkingArea> {
        return if (shouldReturnError || response == null) {
            Result.failure(errorToReturn)
        } else {
            response?.let {
                Result.success(it)
            } ?: Result.failure(errorToReturn)
        }
    }

}