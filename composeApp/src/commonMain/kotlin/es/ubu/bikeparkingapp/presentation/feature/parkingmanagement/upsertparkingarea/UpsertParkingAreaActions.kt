package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.upsertparkingarea

import kotlinx.datetime.DayOfWeek

/**
 * Representa las acciones de la pantalla de agregar o modificar parking.
 * @property onNameChange Función para cambiar el nombre del parking.
 * @property onAddressChange Función para cambiar la dirección del parking.
 * @property onOpeningTimeChange Función para cambiar el horario de apertura.
 * @property onClosingTimeChange Función para cambiar el horario de cierre.
 * @property onCapacityChange Función para cambiar la capacidad del parking.
 * @property onBackClick Función para volver atrás.
 * @property onSaveParkingArea Función para agregar el parking.
 * @property onNavigateToMap Función para navegar a la pantalla de mapa.
 * @property onRuleInputChange Función para cambiar el texto de la regla.
 * @property onAddRule Función para agregar una regla.
 * @property onRemoveRule Función para eliminar una regla.
 * @property validateForm Función para validar el formulario.
 * @property onDayToggle Función para cambiar el estado de un día de apertura.
 * @property toggleOpeningPicker Función para cambiar el estado del selector de horario de apertura.
 * @property toggleClosingPicker Función para cambiar el estado del selector de horario de cierre.
 * @property onOpen24HoursToggle Función para cambiar el estado de si el parking está abierto 24 horas.
 */
data class UpsertParkingAreaActions(
    val onNameChange: (String) -> Unit = {},
    val onAddressChange: (String) -> Unit = {},
    val onOpeningTimeChange: (String) -> Unit = {},
    val onClosingTimeChange: (String) -> Unit = {},
    val onCapacityChange: (Int) -> Unit,
    val onBackClick: () -> Unit,
    val onSaveParkingArea: () -> Unit,
    val onNavigateToMap: () -> Unit,
    val onRuleInputChange: (String) -> Unit,
    val onAddRule: () -> Unit,
    val onRemoveRule: (Int) -> Unit,
    val validateForm: () -> Boolean,
    val onDayToggle: (DayOfWeek) -> Unit,
    val toggleOpeningPicker: (Boolean) -> Unit,
    val toggleClosingPicker: (Boolean) -> Unit,
    val onOpen24HoursToggle: (Boolean) -> Unit
)