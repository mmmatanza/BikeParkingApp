package es.ubu.bikeparkingapp.domain.entity

import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Clase que representa una predicción de ocupación.
 * @property parkingAreaId Identificador del área de parking.
 * @property dateTime Fecha y hora de la predicción.
 * @property predictedOccupancy Ocupación estimada.
 * @property confidenceScore Confianza de la predicción.
 */
@Serializable
data class OccupancyPrediction(
    val parkingAreaId: String,
    val dateTime: Instant,
    val predictedOccupancy: Int,
    val confidenceScore: Double
)
