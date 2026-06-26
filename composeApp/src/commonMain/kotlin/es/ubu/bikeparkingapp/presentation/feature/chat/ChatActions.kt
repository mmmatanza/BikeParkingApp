package es.ubu.bikeparkingapp.presentation.feature.chat

/**
 * Acciones de la pantalla de chat.
 * @property onBackClick Acción al pulsar el botón de atrás.
 * @property onInputChange Acción al cambiar el texto de entrada.
 * @property onSendMessage Acción al enviar un mensaje.
 */
data class ChatActions(
    val onBackClick: () -> Unit = {},
    val onInputChange: (String) -> Unit = {},
    val onSendMessage: () -> Unit = {}
)
