package es.ubu.bikeparkingapp.domain.entity

import kotlinx.serialization.Serializable

/**
 * Representa un conjunto de parkings y el parking recomendado al usuario
 */
@Serializable
data class ParkingDiscovery(
    val recommended: ParkingArea?,
    val allNearby: List<ParkingArea>
)
