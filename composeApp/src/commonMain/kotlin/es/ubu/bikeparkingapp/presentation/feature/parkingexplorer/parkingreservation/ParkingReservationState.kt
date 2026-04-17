package es.ubu.bikeparkingapp.presentation.feature.parkingexplorer.parkingreservation

import es.ubu.bikeparkingapp.domain.entity.ParkingArea

/**
 * Representa el estado de la pantalla de reserva de plaza.
 * @property isLoading Indica si se está cargando la información.
 * @property successfulReservation Indica si se ha realizado la reserva correctamente.
 * @property error Error ocurrido.
 * @property parkingArea Parking al que se reserva.
 * @property confirmReservation Indica si se ha confirmado la reserva.
 */
data class ParkingReservationState(
    val isLoading: Boolean = false,
    val successfulReservation: Boolean = false,
    val error: Exception? = null,
    val parkingArea: ParkingArea? = null,
    val confirmReservationDialog: Boolean = false
)