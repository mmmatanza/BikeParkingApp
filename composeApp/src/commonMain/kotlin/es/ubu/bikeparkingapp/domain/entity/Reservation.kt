package es.ubu.bikeparkingapp.domain.entity

import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Representa los datos de una reserva dentro del sistema.
 * @property reservationId Identificador único de la reserva
 * @property accountId Identificador único del usuario que realiza la reserva
 * @property parkingAreaId Identificador único del parking al que se reserva
 * @property inTime Fecha y hora de inicio de la reserva (el margen de entrada puede variar)
 * @property outTime Fecha y hora de finalización de la reserva
 * @property state Estado actual de la reserva
 * @property createdAt Fecha y hora de creación de la reserva
 */
@Serializable
data class Reservation(
    val reservationId: String?,
    val accountId: String,
    val parkingAreaId: String,
    @Serializable(with = InstantSerializer::class)
    val inTime: Instant,
    @Serializable(with = InstantSerializer::class)
    val outTime: Instant,
    val state: ReservationState,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant
){
    val canCancel: Boolean get() = state == ReservationState.RESERVED
    val canCheckOut: Boolean get() = state == ReservationState.CHECKED_IN
    val hasActions: Boolean get() = canCancel || canCheckOut
}

enum class ReservationState {
    RESERVED, CHECKED_IN, CHECKED_OUT, CANCELLED, EXPIRED, OVERDUE;
    companion object {
        fun fromString(value: String) =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: RESERVED
    }
    fun canTransitionTo(next: ReservationState): Boolean = when (this) {
        RESERVED -> next in listOf(CHECKED_IN, CANCELLED, EXPIRED)
        CHECKED_IN -> next in listOf(CHECKED_OUT, OVERDUE)
        else -> false
    }
}