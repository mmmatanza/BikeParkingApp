package es.ubu.bikeparkingapp.domain.repository

import es.ubu.bikeparkingapp.domain.model.UserLocation

/**
 * Representa el repositorio de ubicación.
 */

interface LocationRepository {
    suspend fun getUserLocation(): UserLocation
}