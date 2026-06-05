package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.parkingmanagement

import es.ubu.bikeparkingapp.domain.entity.OccupancyPrediction
import es.ubu.bikeparkingapp.domain.entity.ParkingArea

/**
 * Representa el estado de la pantalla de detalle de un parking.
 * @property isLoading Indica si se está cargando la información.
 * @property showDeactivateDialog Indica si se debe mostrar el diálogo de desactivar el parking.
 * @property showToggleDialog Indica si se debe mostrar el diálogo de cambiar el estado operativo del parking.
 * @property parking Información del parking.
 * @property predictedOccupancy Ocupación predicha.
 * @property error Error que se ha producido.
 * @property successDeactivation Indica si se ha realizado la acción de desactivar el parking.
 */
data class ParkingManagementState(
    val isLoading: Boolean = true,
    val showDeactivateDialog: Boolean = false,
    val showToggleDialog: Boolean = false,
    val parking: ParkingArea? = null,
    val predictedOccupancy: OccupancyPrediction? = null,
    val error: Exception? = null,
    val successDeactivation: Boolean = false
)