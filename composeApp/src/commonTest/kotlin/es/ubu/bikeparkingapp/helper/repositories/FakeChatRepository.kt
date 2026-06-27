package es.ubu.bikeparkingapp.helper.repositories

import es.ubu.bikeparkingapp.domain.entity.ChatMessage
import es.ubu.bikeparkingapp.domain.entity.MessageRole
import es.ubu.bikeparkingapp.domain.repository.ChatRepository
import kotlin.time.Clock

class FakeChatRepository : ChatRepository {
    private val messages = mutableMapOf<String, MutableList<ChatMessage>>()
    var shouldReturnError = false

    override suspend fun getChatHistory(accountId: String): Result<List<ChatMessage>> {
        return if (shouldReturnError) {
            Result.failure(Exception("Error al obtener el historial"))
        } else {
            Result.success(messages[accountId] ?: emptyList())
        }
    }

    override suspend fun sendMessage(accountId: String, content: String): Result<ChatMessage> {
        if (shouldReturnError) return Result.failure(Exception("Error al enviar el mensaje"))
        
        val now = Clock.System.now()
        
        val userMessage = ChatMessage(
            messageId = "msg_user_${now.toEpochMilliseconds()}",
            accountId = accountId,
            role = MessageRole.USER,
            content = content,
            createdAt = now
        )
        
        val aiMessage = ChatMessage(
            messageId = "msg_ai_${now.toEpochMilliseconds()}",
            accountId = accountId,
            role = MessageRole.ASSISTANT,
            content = "Respuesta simulada para: $content",
            createdAt = now
        )
        
        val history = messages.getOrPut(accountId) { mutableListOf() }
        history.add(userMessage)
        history.add(aiMessage)
        
        return Result.success(aiMessage)
    }
}
