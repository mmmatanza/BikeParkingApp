package es.ubu.bikeparkingapp.helper.repositories

import es.ubu.bikeparkingapp.domain.model.UserLocation
import es.ubu.bikeparkingapp.domain.repository.LocationRepository

class FakeLocationRepository : LocationRepository {
    override suspend fun getUserLocation(): UserLocation {
        return UserLocation(latitude = 0.0, longitude = 0.0)
    }
}