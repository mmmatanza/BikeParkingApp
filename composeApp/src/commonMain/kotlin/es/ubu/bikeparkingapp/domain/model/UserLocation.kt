package es.ubu.bikeparkingapp.domain.model

/**
 * Representa la ubicación del usuario.
 *
 * @property latitude Latitud del usuario.
 * @property longitude Longitud del usuario.
 */

data class UserLocation(
    val latitude: Double,
    val longitude: Double
)