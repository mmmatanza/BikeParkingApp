package es.ubu.bikeparkingapp.domain.repository

import es.ubu.bikeparkingapp.domain.entity.ChatMessage

/**
 * Interfaz del repositorio para la gestión de mensajes del chat inteligente.
 */
interface ChatRepository {
    suspend fun getChatHistory(accountId: String): Result<List<ChatMessage>>
    suspend fun sendMessage(accountId: String, content: String): Result<ChatMessage>
}
