package es.ubu.bikeparkingapp.domain.usecase.chat

import es.ubu.bikeparkingapp.domain.entity.ChatMessage

/**
 * Interfaz del caso de uso para enviar un mensaje al chat.
 */
interface SendChatMessageUseCase {
    suspend operator fun invoke(accountId: String, content: String): Result<ChatMessage>
}
