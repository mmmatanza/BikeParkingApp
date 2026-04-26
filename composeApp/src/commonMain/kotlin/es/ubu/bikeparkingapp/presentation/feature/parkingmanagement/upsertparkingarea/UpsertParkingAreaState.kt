package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.upsertparkingarea

import kotlinx.datetime.DayOfWeek

/**
 * Representa el estado de la pantalla de agregar o modificar parking.
 * @property parkingAreaId Id del parking.
 * @property isEditing Indica si se está editando un parking existente.
 * @property error Error ocurrido durante la agregación de parking
 * @property name Nombre del parking.
 * @property address Dirección del parking.
 * @property capacity Capacidad máxima del parking.
 * @property openingTime Horario de apertura del parking.
 * @property closingTime Horario de cierre del parking.
 * @property openDays Días de apertura del parking.
 * @property latitude Latitud de la ubicación del parking.
 * @property longitude Longitud de la ubicación del parking.
 * @property isSuccess Indica si la agregación ha sido exitosa.
 * @property rules Lista de reglas del parking.
 * @property currentRuleInput Regla actual.
 * @property showOpeningPicker Indica si se debe mostrar el diálogo para seleccionar la hora de apertura.
 * @property showClosingPicker Indica si se debe mostrar el diálogo para seleccionar la hora de cierre.
 * @property isOpen24Hours Indica si el parking está abierto 24 horas.
 * @property upsertConfirmationDialog Indica si se debe mostrar el diálogo de confirmación de agregación.
 */
data class UpsertParkingAreaState(
    val parkingAreaId: String? = null,
    val error: Exception? = null,
    val name: String = "",
    val address: String = "",
    val capacity: Int = 0,
    val openingTime: String = "00:00",
    val closingTime: String = "23:59",
    val openDays: Set<DayOfWeek> = emptySet(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val isAlreadyLoaded: Boolean = false,
    val isSuccess: Boolean = false,
    val rules: List<String> = emptyList(),
    val currentRuleInput: String = "",
    val showOpeningPicker: Boolean = false,
    val showClosingPicker: Boolean = false,
    val isOpen24Hours: Boolean = false,
    val upsertConfirmationDialog : Boolean = false,
)