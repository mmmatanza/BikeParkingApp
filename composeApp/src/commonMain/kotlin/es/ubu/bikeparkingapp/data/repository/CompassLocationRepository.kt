package es.ubu.bikeparkingapp.data.repository

import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.GeolocatorResult
import es.ubu.bikeparkingapp.domain.model.UserLocation
import es.ubu.bikeparkingapp.domain.repository.LocationRepository

/**
 * Implementación del repositorio de ubicación que utiliza Geolocator para obtener la ubicación del usuario.
 * @property geolocator Geolocator para obtener la ubicación del usuario.
 */
class CompassLocationRepository(
    private val geolocator: Geolocator
) : LocationRepository {

    override suspend fun getUserLocation(): UserLocation {
        // Solicitamos alta precisión para que el marcador sea preciso en el móvil
        return when (val result = geolocator.current(Priority.HighAccuracy)) {
            is GeolocatorResult.Success -> UserLocation(
                latitude = result.data.coordinates.latitude,
                longitude = result.data.coordinates.longitude
            )
            is GeolocatorResult.Error -> throw Exception(result.message)
        }
    }

}
