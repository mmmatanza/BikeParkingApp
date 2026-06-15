package es.ubu.bikeparkingapp.domain.usecase.eco

import es.ubu.bikeparkingapp.domain.entity.AdminEcoMetrics

/**
 * Interfaz del caso de uso para obtener las métricas ecológicas de un parking para un administrador.
 */
interface GetAdminEcoMetricsUseCase {
    suspend operator fun invoke(parkingAreaId: String): Result<AdminEcoMetrics>
}
