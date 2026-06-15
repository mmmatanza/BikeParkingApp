package es.ubu.bikeparkingapp.domain.entity

import kotlinx.serialization.Serializable

/**
 * Representa las métricas ecológicas de un usuario individual.
 */
@Serializable
data class UserEcoMetrics(
    val weeklyMetrics: UserPeriodMetrics,
    val monthlyMetrics: UserPeriodMetrics,
    val yearlyMetrics: UserPeriodMetrics
)

/**
 * Métricas para un periodo específico.
 */
@Serializable
data class UserPeriodMetrics(
    val distance: Double,
    val rankingPosition: Int,
    val totalUsers: Int
)
