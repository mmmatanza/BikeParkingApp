package es.ubu.bikeparkingapp.helper.usecases.location

import es.ubu.bikeparkingapp.domain.model.UserLocation
import es.ubu.bikeparkingapp.domain.repository.LocationRepository

class FakeGetUserLocationRepository : LocationRepository {

    var locationToReturn: UserLocation = UserLocation(0.0, 0.0)
    var shouldReturnError = false
    var errorToReturn: Throwable = Exception("Fake error")

    override suspend fun getUserLocation(): UserLocation {
        if (shouldReturnError) {
            throw errorToReturn
        }
        return locationToReturn
    }
}