package es.ubu.bikeparkingapp.helper.repositories

import es.ubu.bikeparkingapp.domain.entity.AdminEcoMetrics
import es.ubu.bikeparkingapp.domain.entity.UserEcoMetrics
import es.ubu.bikeparkingapp.domain.repository.EcoMetricsRepository

class FakeEcoMetricsRepository : EcoMetricsRepository {
    var adminMetrics: AdminEcoMetrics? = null
    var userMetrics: UserEcoMetrics? = null
    var shouldReturnError = false

    override suspend fun getAdminEcoMetrics(parkingAreaId: String): Result<AdminEcoMetrics> {
        return if (shouldReturnError) Result.failure(Exception("Error"))
        else adminMetrics?.let { Result.success(it) } ?: Result.failure(Exception("Not found"))
    }

    override suspend fun getUserEcoMetrics(userId: String): Result<UserEcoMetrics> {
        return if (shouldReturnError) Result.failure(Exception("Error"))
        else userMetrics?.let { Result.success(it) } ?: Result.failure(Exception("Not found"))
    }
}
