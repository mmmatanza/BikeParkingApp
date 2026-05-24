package es.ubu.bikeparkingapp.domain.usecase.alert

import es.ubu.bikeparkingapp.domain.entity.Alert
import es.ubu.bikeparkingapp.domain.repository.AlertRepository
import es.ubu.bikeparkingapp.domain.repository.AuthRepository

/**
 * Implementación del caso de uso para obtener alertas.
 */
class GetAlertsUseCaseImpl(
    private val alertRepository: AlertRepository,
    private val authRepository: AuthRepository
) : GetAlertsUseCase {
    override suspend fun invoke(): Result<List<Alert>> {
        return authRepository.getCurrentUserId().fold(
            onSuccess = { userId ->
                alertRepository.getAlertsByAccountId(userId)
            },
            onFailure = { Result.failure(it) }
        )
    }
}
