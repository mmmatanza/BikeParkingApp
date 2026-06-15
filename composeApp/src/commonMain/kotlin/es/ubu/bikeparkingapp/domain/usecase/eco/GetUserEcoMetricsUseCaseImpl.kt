package es.ubu.bikeparkingapp.domain.usecase.eco

import es.ubu.bikeparkingapp.domain.entity.UserEcoMetrics
import es.ubu.bikeparkingapp.domain.repository.AuthRepository
import es.ubu.bikeparkingapp.domain.repository.EcoMetricsRepository

/**
 * Implementación del caso de uso para obtener las métricas ecológicas del usuario actual.
 */
class GetUserEcoMetricsUseCaseImpl(
    private val authRepository: AuthRepository,
    private val ecoMetricsRepository: EcoMetricsRepository
) : GetUserEcoMetricsUseCase {
    override suspend fun invoke(): Result<UserEcoMetrics> {
        return authRepository.getCurrentUserId().fold(
            onSuccess = { userId ->
                ecoMetricsRepository.getUserEcoMetrics(userId)
            },
            onFailure = {
                Result.failure(it)
            }
        )
    }
}
