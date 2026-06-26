package es.ubu.bikeparkingapp.presentation.feature.chat

import es.ubu.bikeparkingapp.domain.entity.ChatMessage

/**
 * Representa el estado del chat.
 * @property messages Lista de mensajes del chat.
 * @property currentInput Mensaje actual en el campo de entrada.
 * @property isLoading Indica si se está cargando el chat.
 * @property error Mensaje de error en caso de que ocurra.
 */
data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val currentInput: String = "",
    val isLoading: Boolean = false,
    val error: Throwable? = null
)
