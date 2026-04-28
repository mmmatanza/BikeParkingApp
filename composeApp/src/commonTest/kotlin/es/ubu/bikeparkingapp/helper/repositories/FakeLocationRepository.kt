package es.ubu.bikeparkingapp.helper.repositories

import es.ubu.bikeparkingapp.domain.model.UserLocation
import es.ubu.bikeparkingapp.domain.repository.LocationRepository

class FakeLocationRepository : LocationRepository {

    var locationToReturn = UserLocation(latitude = 0.0, longitude = 0.0)
    var shouldThrowException = false

    override suspend fun getUserLocation(): UserLocation {
        if (shouldThrowException) throw Exception("Location error")
        return locationToReturn
    }
}