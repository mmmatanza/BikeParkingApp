package es.ubu.bikeparkingapp.domain.usecase.alert

import es.ubu.bikeparkingapp.domain.repository.AlertRepository

/**
 * Implementación del caso de uso para marcar una alerta como leída.
 */
class MarkAlertAsReadUseCaseImpl(
    private val alertRepository: AlertRepository
) : MarkAlertAsReadUseCase {
    override suspend fun invoke(alertId: String): Result<Unit> {
        return alertRepository.markAsRead(alertId)
    }
}
