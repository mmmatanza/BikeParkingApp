package es.ubu.bikeparkingapp.helper.usecases.location

import es.ubu.bikeparkingapp.domain.model.UserLocation
import es.ubu.bikeparkingapp.domain.usecase.location.GetUserLocationUseCase

class FakeGetUserLocationUseCase : GetUserLocationUseCase {
    var response: UserLocation = UserLocation(0.0, 0.0)
    var shouldReturnError = false
    var errorToReturn: Throwable = Exception("Fake error")

    override suspend fun invoke(): Result<UserLocation> {
        return if (shouldReturnError) {
            Result.failure(errorToReturn)
        } else {
            Result.success(response)
        }
    }
}