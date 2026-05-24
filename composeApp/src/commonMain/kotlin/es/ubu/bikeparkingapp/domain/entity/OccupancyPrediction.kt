package es.ubu.bikeparkingapp.domain.entity

import kotlin.time.Instant
import kotlinx.serialization.Serializable

/**
 * Clase que representa una predicción de ocupación.
 */
@Serializable
data class OccupancyPrediction(
    val parkingAreaId: String,
    val dateTime: Instant,
    val predictedOccupancy: Int,
    val confidenceScore: Double
)
