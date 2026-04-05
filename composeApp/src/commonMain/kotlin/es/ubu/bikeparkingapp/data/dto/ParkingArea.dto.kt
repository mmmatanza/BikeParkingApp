package es.ubu.bikeparkingapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Representa los datos de un parking obtenidos de la base de datos.
 *
 * @property id Identificador del parking
 * @property ownerId Identificador del propietario
 * @property name Nombre del parking
 * @property latitude Latitud del parking
 * @property longitude Longitud del parking
 * @property capacity Capacidad del parking
 * @property currentOccupancy Plazas ocupadas
 * @property isOperative Estado del parking (operativo/fuera de servicio)
 * @property isActive Indica si sigue activo el parking o ha sido dado de baja
 * @property openingTime Horario de apertura
 * @property closingTime Horario de cierre
 * @property rules Reglas del parking
 */
@Serializable
data class ParkingAreaDto(
    @SerialName("parking_area_id") val id: String? = null,
    @SerialName("owner_id") val ownerId: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val capacity: Int,
    @SerialName("current_occupancy") val currentOccupancy: Int,
    @SerialName("is_operative") val isOperative: Boolean,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("opening_time") val openingTime: String,
    @SerialName("closing_time") val closingTime: String,
    val rules: List<String> = emptyList()
)