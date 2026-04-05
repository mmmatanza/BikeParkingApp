package es.ubu.bikeparkingapp.presentation.feature.parking.addparkingarea

/**
 * Representa el estado de la pantalla de agregar parking.
 * @property parkingId Id del parking.
 * @property isEditing Indica si se está editando un parking existente.
 * @property error Error ocurrido durante la agregación de parking
 * @property name Nombre del parking.
 * @property capacity Capacidad máxima del parking.
 * @property openingTime Horario de apertura del parking.
 * @property closingTime Horario de cierre del parking.
 * @property latitude Latitud de la ubicación del parking.
 * @property longitude Longitud de la ubicación del parking.
 * @property isSuccess Indica si la agregación ha sido exitosa.
 * @property rules Lista de reglas del parking.
 * @property currentRuleInput Regla actual.
 */
data class AddParkingAreaState(
    val parkingId: String? = null,
    val error: Exception? = null,
    val name: String = "",
    val capacity: Int = 0,
    val openingTime: String = "00:00",
    val closingTime: String = "23:59",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val rules: List<String> = emptyList(),
    val currentRuleInput: String = ""
)