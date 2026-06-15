package es.ubu.bikeparkingapp.domain.usecase.eco

import es.ubu.bikeparkingapp.domain.entity.AdminEcoMetrics
import es.ubu.bikeparkingapp.domain.repository.EcoMetricsRepository

/**
 * Implementación del caso de uso para obtener las métricas ecológicas de un parking para un administrador.
 * @property ecoMetricsRepository Repositorio de métricas ecológicas.
 */
class GetAdminEcoMetricsUseCaseImpl(
    private val ecoMetricsRepository: EcoMetricsRepository
) : GetAdminEcoMetricsUseCase {
    override suspend fun invoke(parkingAreaId: String): Result<AdminEcoMetrics> {
        return ecoMetricsRepository.getAdminEcoMetrics(parkingAreaId)
    }
}
