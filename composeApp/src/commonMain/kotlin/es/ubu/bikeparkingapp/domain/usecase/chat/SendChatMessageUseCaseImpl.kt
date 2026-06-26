package es.ubu.bikeparkingapp.domain.usecase.chat

import es.ubu.bikeparkingapp.domain.entity.ChatMessage
import es.ubu.bikeparkingapp.domain.repository.ChatRepository

/**
 * Implementación del caso de uso para enviar un mensaje al chat.
 */
class SendChatMessageUseCaseImpl(
    private val chatRepository: ChatRepository
) : SendChatMessageUseCase {
    override suspend fun invoke(accountId: String, content: String): Result<ChatMessage> {
        if (content.isBlank()) return Result.failure(Exception("Message content cannot be empty"))
        return chatRepository.sendMessage(accountId, content)
    }
}
