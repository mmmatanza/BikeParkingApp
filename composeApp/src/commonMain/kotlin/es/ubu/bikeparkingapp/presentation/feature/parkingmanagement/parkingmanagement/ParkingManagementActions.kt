package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.parkingmanagement

/**
 * Representa las acciones de la pantalla de detalle de un parking.
 * @property onViewOccupancyClick Acción al hacer click en el botón de ver ocupación.
 * @property onEditDetailsClick Acción al hacer click en el botón de editar detalles.
 * @property onToggleServiceClick Acción al hacer click en el botón de cambiar el estado operativo.
 * @property onDeactivateClick Acción al hacer click en el botón de desactivar el parking.
 * @property onBackClick Acción al hacer click en el botón de volver.
 */
data class ParkingManagementActions(
    val onViewOccupancyClick: (String) -> Unit = {},
    val onEditDetailsClick: (String) -> Unit = {},
    val onToggleServiceClick: () -> Unit = {},
    val onDeactivateClick: () -> Unit = {},
    val onBackClick: () -> Unit = {}
)