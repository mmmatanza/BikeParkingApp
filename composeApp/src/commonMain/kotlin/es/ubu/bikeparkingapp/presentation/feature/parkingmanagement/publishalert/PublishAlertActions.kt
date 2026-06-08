package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.publishalert

/**
 * Acciones que se pueden realizar en la pantalla de publicar alerta.
 *
 * @property onMessageChange Acción al cambiar el mensaje de la alerta.
 * @property onSendClick Acción al pulsar en enviar la alerta.
 * @property onBackClick Acción al pulsar en volver atrás.
 */
data class PublishAlertActions(
    val onMessageChange: (String) -> Unit,
    val onSendClick: () -> Unit,
    val onBackClick: () -> Unit
)
