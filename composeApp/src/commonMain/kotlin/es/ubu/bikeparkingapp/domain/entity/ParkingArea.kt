package es.ubu.bikeparkingapp.domain.entity

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlin.time.Clock

/**
 * Representa a un parking dentro del sistema.
 * @property parkingAreaId Identificador único del parking.
 * @property ownerId Identificador único del propietario del parking.
 * @property name Nombre del parking.
 * @property address Dirección del parking.
 * @property latitude Latitud del parking.
 * @property longitude Longitud del parking.
 * @property capacity Capacidad máxima del parking.
 * @property currentOccupancy Número actual de vehículos ocupados en el parking.
 * @property isOperative Indica si el parking está operativo o no.
 * @property isActive Indica si el parking está activo o no.
 * @property timezoneId Zona horaria del parking.
 * @property openingTime Horario de apertura del parking.
 * @property closingTime Horario de cierre del parking.
 * @property openDays Días de apertura del parking.
 * @property rules Lista de reglas del parking.
 */
@Serializable
data class ParkingArea(
    val parkingAreaId: String?,
    val ownerId: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val capacity: Int,
    val currentOccupancy: Int,
    val isOperative: Boolean,
    val isActive: Boolean,
    val timezoneId: String = "Europe/Madrid",
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
    // Obtenemos el "ahora" pero proyectado en la zona horaria del parking
    val parkingTimeZone = TimeZone.of(this.timezoneId)
    val nowInParking = Clock.System.now().toLocalDateTime(parkingTimeZone)

    // Comprobamos si el día de hoy es día de apertura
    if (nowInParking.dayOfWeek !in openDays) {
        return false
    }

    // Comprobamos el rango horario usando la hora local del parking
    val currentTime = nowInParking.time
    val opening = LocalTime.parse(openingTime)
    val closing = LocalTime.parse(closingTime)

    return currentTime in opening..closing
}