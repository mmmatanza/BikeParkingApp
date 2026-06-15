package es.ubu.bikeparkingapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParkingDistanceDto(
    val period: String,
    @SerialName("total_distance") val totalDistance: Double
)

@Serializable
data class ParkingTopUserDto(
    val period: String,
    @SerialName("user_name") val userName: String,
    @SerialName("total_distance") val totalDistance: Double
)
