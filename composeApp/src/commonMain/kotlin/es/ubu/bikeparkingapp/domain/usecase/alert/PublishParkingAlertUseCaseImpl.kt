package es.ubu.bikeparkingapp.domain.usecase.alert

import es.ubu.bikeparkingapp.domain.repository.AlertRepository

/**
 * Implementación del caso de uso para publicar una alerta manual en un parking.
 * @property repository Repositorio de alertas.
 */
class PublishParkingAlertUseCaseImpl(
    private val repository: AlertRepository
) : PublishParkingAlertUseCase {
    override suspend fun invoke(parkingId: String, message: String): Result<Unit> {
        if (message.isBlank()) {
            return Result.failure(Exception(""))
        }
        return repository.publishParkingAlert(parkingId, message)
    }
}
