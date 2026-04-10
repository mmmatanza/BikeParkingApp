package es.ubu.bikeparkingapp.domain.entity

import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable
import kotlin.time.Clock

/**
 * Representa a un parking dentro del sistema.
 * @property id Identificador único del parking.
 * @property ownerId Identificador único del propietario del parking.
 * @property name Nombre del parking.
 * @property address Dirección del parking.
 * @property latitude Latitud del parking.
 * @property longitude Longitud del parking.
 * @property capacity Capacidad máxima del parking.
 * @property currentOccupancy Número actual de vehículos ocupados en el parking.
 * @property isOperative Indica si el parking está operativo o no.
 * @property isActive Indica si el parking está activo o no.
 * @property openingTime Horario de apertura del parking.
 * @property closingTime Horario de cierre del parking.
 * @property openDays Días de apertura del parking.
 * @property rules Lista de reglas del parking.
 */
@Serializable
data class ParkingArea(
    val id: String?,
    val ownerId: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val capacity: Int,
    val currentOccupancy: Int,
    val isOperative: Boolean,
    val isActive: Boolean,
    val openingTime: String,
    val closingTime: String,
    val openDays: Set<DayOfWeek>,
    val rules: List<String> = emptyList()
)

/**
 * Función para determinar si un parking está abierto o cerrado.
 * @return `true` si el parking está abierto, `false` en caso contrario.
 */
fun ParkingArea.isOpen(): Boolean {
    val now = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).time
    val opening = LocalTime.parse(openingTime)
    val closing = LocalTime.parse(closingTime)
    return now in opening..closing
}