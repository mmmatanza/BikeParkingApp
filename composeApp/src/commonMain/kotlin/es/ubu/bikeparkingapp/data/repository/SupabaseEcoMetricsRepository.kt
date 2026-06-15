package es.ubu.bikeparkingapp.data.repository

import es.ubu.bikeparkingapp.data.dto.ParkingDistanceDto
import es.ubu.bikeparkingapp.data.dto.ParkingTopUserDto
import es.ubu.bikeparkingapp.data.dto.UserEcoMetricsDto
import es.ubu.bikeparkingapp.domain.entity.AdminEcoMetrics
import es.ubu.bikeparkingapp.domain.entity.UserEcoMetrics
import es.ubu.bikeparkingapp.domain.entity.UserPeriodMetrics
import es.ubu.bikeparkingapp.domain.entity.UserRanking
import es.ubu.bikeparkingapp.domain.repository.EcoMetricsRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Repositorio de métricas ecológicas.
 * @param client Cliente Supabase.
 */
class SupabaseEcoMetricsRepository(
    private val client: SupabaseClient
) : EcoMetricsRepository {

    override suspend fun getAdminEcoMetrics(parkingAreaId: String): Result<AdminEcoMetrics> = runCatching {
        // Obtenemos las distancias totales por periodo
        val distances = client.postgrest.rpc(
            function = "get_parking_eco_metrics",
            parameters = buildJsonObject { put("p_parking_area_id", parkingAreaId) }
        ).decodeList<ParkingDistanceDto>()

        // Obtenemos los tops de usuarios por periodo
        val topUsers = client.postgrest.rpc(
            function = "get_parking_top_users",
            parameters = buildJsonObject { put("p_parking_area_id", parkingAreaId) }
        ).decodeList<ParkingTopUserDto>()

        AdminEcoMetrics(
            weeklyDistance = distances.find { it.period == "WEEK" }?.totalDistance ?: 0.0,
            monthlyDistance = distances.find { it.period == "MONTH" }?.totalDistance ?: 0.0,
            yearlyDistance = distances.find { it.period == "YEAR" }?.totalDistance ?: 0.0,
            weeklyTopUsers = topUsers.filter { it.period == "WEEK" }.map { UserRanking(it.userName, it.totalDistance) },
            monthlyTopUsers = topUsers.filter { it.period == "MONTH" }.map { UserRanking(it.userName, it.totalDistance) },
            yearlyTopUsers = topUsers.filter { it.period == "YEAR" }.map { UserRanking(it.userName, it.totalDistance) }
        )
    }

    override suspend fun getUserEcoMetrics(userId: String): Result<UserEcoMetrics> = runCatching {
        val results = client.postgrest.rpc(
            function = "get_user_eco_metrics",
            parameters = buildJsonObject { put("p_user_id", userId) }
        ).decodeList<UserEcoMetricsDto>()

        fun mapToPeriod(period: String) = results.find { it.period == period }?.let {
            UserPeriodMetrics(it.userDistance, it.rankingPosition, it.totalUsers)
        } ?: UserPeriodMetrics(0.0, 0, 0)

        UserEcoMetrics(
            weeklyMetrics = mapToPeriod("WEEK"),
            monthlyMetrics = mapToPeriod("MONTH"),
            yearlyMetrics = mapToPeriod("YEAR")
        )
    }
}
