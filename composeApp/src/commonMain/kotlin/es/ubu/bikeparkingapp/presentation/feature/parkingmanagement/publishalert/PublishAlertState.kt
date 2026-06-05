package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.publishalert

/**
 * Estado de la pantalla de emitir alerta.
 * @property message Mensaje de la alerta.
 * @property isLoading Indica si se está cargando.
 * @property error Error ocurrido.
 * @property isSuccess Indica si la operación ha sido exitosa.
 */
data class PublishAlertState(
    val message: String = "",
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val isSuccess: Boolean = false
)
