package es.ubu.bikeparkingapp.domain.usecase.eco

import es.ubu.bikeparkingapp.domain.entity.UserEcoMetrics

/**
 * Interfaz del caso de uso para obtener las métricas ecológicas del usuario actual.
 */
interface GetUserEcoMetricsUseCase {
    suspend operator fun invoke(): Result<UserEcoMetrics>
}
