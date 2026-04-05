package es.ubu.bikeparkingapp.domain.usecase.location

import es.ubu.bikeparkingapp.domain.model.UserLocation

/**
 * Interfaz del caso de uso para obtener la ubicación del usuario.
 */
interface GetUserLocationUseCase {
    suspend operator fun invoke(): Result<UserLocation>
}