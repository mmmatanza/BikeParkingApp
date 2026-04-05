package es.ubu.bikeparkingapp.presentation.feature.parking.parkingmanagement

/**
 * Representa las acciones de la pantalla de detalle de un parking.
 * @property onViewOccupancyClick Acción al hacer click en el botón de ver ocupación.
 * @property onEditDetailsClick Acción al hacer click en el botón de editar detalles.
 * @property onToggleServiceClick Acción al hacer click en el botón de cambiar el estado operativo.
 * @property onToggleServiceDismiss Acción al hacer click en el botón de cancelar el cambio de estado operativo.
 * @property onToggleServiceConfirm Acción al hacer click en el botón de confirmar el cambio de estado operativo.
 * @property onDeactivateClick Acción al hacer click en el botón de desactivar el parking.
 * @property onDeactivateDialogDismiss Acción al hacer click en el botón de cancelar la acción de desactivar el parking.
 * @property onDeactivateConfirm Acción al hacer click en el botón de confirmar la acción de desactivar el parking.
 * @property onBackClick Acción al hacer click en el botón de volver.
 */
data class ParkingManagementActions(
    val onViewOccupancyClick: () -> Unit = {},
    val onEditDetailsClick: (String) -> Unit = {},
    val onToggleServiceClick: () -> Unit = {},
    val onToggleServiceDismiss: () -> Unit = {},
    val onToggleServiceConfirm: () -> Unit = {},
    val onDeactivateClick: () -> Unit = {},
    val onDeactivateDialogDismiss: () -> Unit = {},
    val onDeactivateConfirm: () -> Unit = {},
    val onBackClick: () -> Unit = {}
)