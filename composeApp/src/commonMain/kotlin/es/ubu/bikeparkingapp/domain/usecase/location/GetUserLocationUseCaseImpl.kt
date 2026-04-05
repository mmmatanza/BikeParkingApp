package es.ubu.bikeparkingapp.domain.usecase.location

import es.ubu.bikeparkingapp.domain.model.UserLocation
import es.ubu.bikeparkingapp.domain.repository.LocationRepository

/**
 * Implementación del caso de uso para obtener la ubicación del usuario.
 */
class GetUserLocationUseCaseImpl(
    private val locationRepository: LocationRepository
) : GetUserLocationUseCase {
    override suspend operator fun invoke(): Result<UserLocation> {
        return runCatching {
            locationRepository.getUserLocation()
        }
    }
}