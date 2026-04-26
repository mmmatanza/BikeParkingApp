package es.ubu.bikeparkingapp.presentation.common.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/**
 * Formatea un [Instant] en una cadena de texto legible siguiendo el patrón "dd/MM/yyyy HH:mm"
 * @param instant Instante a formatear
 * @return Cadena de texto formateada
 */
fun formatInstant(instant: Instant): String {
    return try {
        // Convertimos a la zona horaria del sistema del usuario
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        // Formateo
        with(localDateTime) {
            val d = day.toString().padStart(2, '0')
            val m = month.number.toString().padStart(2, '0')
            val y = year
            val hh = hour.toString().padStart(2, '0')
            val mm = minute.toString().padStart(2, '0')
            "$d/$m/$y $hh:$mm"
        }
    } catch (_: Exception) {
        // En caso de error
        instant.toString()
    }
}
