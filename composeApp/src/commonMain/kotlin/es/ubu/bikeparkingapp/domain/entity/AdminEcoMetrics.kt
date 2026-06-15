package es.ubu.bikeparkingapp.domain.entity

import kotlinx.serialization.Serializable

/**
 * Representa las métricas ecológicas de un parking para el administrador.
 * Incluye distancias totales y el ranking de usuarios más ecológicos.
 */
@Serializable
data class AdminEcoMetrics(
    val weeklyDistance: Double,
    val monthlyDistance: Double,
    val yearlyDistance: Double,
    val weeklyTopUsers: List<UserRanking>,
    val monthlyTopUsers: List<UserRanking>,
    val yearlyTopUsers: List<UserRanking>
)

/**
 * Representa a un usuario en el ranking ecológico.
 */
@Serializable
data class UserRanking(
    val userName: String,
    val totalDistance: Double
)
