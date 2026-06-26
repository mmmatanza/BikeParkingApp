package es.ubu.bikeparkingapp.domain.entity

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Representa un mensaje de chat.
 * @property messageId Identificador único del mensaje.
 * @property accountId Identificador del autor del mensaje.
 * @property role Autor del mensaje.
 * @property content Contenido del mensaje.
 * @property createdAt Fecha y hora de creación del mensaje.
 */
@Serializable
data class ChatMessage(
    val messageId: String? = null,
    val accountId: String,
    val role: MessageRole,
    val content: String,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant
)

enum class MessageRole {
    USER, ASSISTANT
}
