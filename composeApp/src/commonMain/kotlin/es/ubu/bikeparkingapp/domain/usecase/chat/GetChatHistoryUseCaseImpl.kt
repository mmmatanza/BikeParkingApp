package es.ubu.bikeparkingapp.domain.usecase.chat

import es.ubu.bikeparkingapp.domain.entity.ChatMessage
import es.ubu.bikeparkingapp.domain.repository.ChatRepository

/**
 * Implementación del caso de uso para obtener el historial del chat.
 */
class GetChatHistoryUseCaseImpl(
    private val chatRepository: ChatRepository
) : GetChatHistoryUseCase {
    override suspend fun invoke(accountId: String): Result<List<ChatMessage>> {
        return chatRepository.getChatHistory(accountId)
    }
}
