package es.ubu.bikeparkingapp.domain.repository

import es.ubu.bikeparkingapp.domain.entity.AdminEcoMetrics
import es.ubu.bikeparkingapp.domain.entity.UserEcoMetrics

/**
 * Repositorio para obtener métricas ecológicas.
 */
interface EcoMetricsRepository {
    suspend fun getAdminEcoMetrics(parkingAreaId: String): Result<AdminEcoMetrics>
    suspend fun getUserEcoMetrics(userId: String): Result<UserEcoMetrics>
}
