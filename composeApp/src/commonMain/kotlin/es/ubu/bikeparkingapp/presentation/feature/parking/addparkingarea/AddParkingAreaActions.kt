package es.ubu.bikeparkingapp.presentation.feature.parking.addparkingarea

/**
 * Representa las acciones de la pantalla de agregar parking.
 * @property onNameChange Función para cambiar el nombre del parking.
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
 */
data class AddParkingAreaActions(
    val onNameChange: (String) -> Unit = {},
    val onOpeningTimeChange: (String) -> Unit = {},
    val onClosingTimeChange: (String) -> Unit = {},
    val onCapacityChange: (Int) -> Unit,
    val onBackClick: () -> Unit,
    val onSaveParkingArea: () -> Unit,
    val onNavigateToMap: () -> Unit,
    val onRuleInputChange: (String) -> Unit,
    val onAddRule: () -> Unit,
    val onRemoveRule: (Int) -> Unit,
    val validateForm: () -> Boolean
)