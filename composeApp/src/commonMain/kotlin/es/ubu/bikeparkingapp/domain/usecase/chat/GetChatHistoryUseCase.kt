package es.ubu.bikeparkingapp.domain.usecase.chat

import es.ubu.bikeparkingapp.domain.entity.ChatMessage

/**
 * Interfaz del caso de uso para obtener el historial de chat.
 */
interface GetChatHistoryUseCase {
    suspend operator fun invoke(accountId: String): Result<List<ChatMessage>>
}
