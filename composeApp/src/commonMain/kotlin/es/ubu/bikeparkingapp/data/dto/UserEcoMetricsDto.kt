package es.ubu.bikeparkingapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO para la respuesta de la función RPC get_user_eco_metrics.
 */
@Serializable
data class UserEcoMetricsDto(
    val period: String,
    @SerialName("user_distance") val userDistance: Double,
    @SerialName("ranking_position") val rankingPosition: Int,
    @SerialName("total_users") val totalUsers: Int
)
