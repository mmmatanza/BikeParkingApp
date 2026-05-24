package es.ubu.bikeparkingapp.domain.usecase.alert

import es.ubu.bikeparkingapp.domain.repository.AlertRepository
import es.ubu.bikeparkingapp.domain.repository.AuthRepository

/**
 * Implementación del caso de uso para marcar todas las alertas como leídas.
 */
class MarkAllAlertsAsReadUseCaseImpl(
    private val alertRepository: AlertRepository,
    private val authRepository: AuthRepository
) : MarkAllAlertsAsReadUseCase {
    override suspend fun invoke(): Result<Unit> {
        return authRepository.getCurrentUserId().fold(
            onSuccess = { userId ->
                alertRepository.markAllAsRead(userId)
            },
            onFailure = { Result.failure(it) }
        )
    }
}
