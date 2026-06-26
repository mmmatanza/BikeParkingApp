package es.ubu.bikeparkingapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Representa un mensaje de chat.
 */
@Serializable
data class ChatMessageDto(
    @SerialName("message_id")
    val messageId: String? = null,
    @SerialName("account_id")
    val accountId: String,
    @SerialName("role")
    val role: String,
    @SerialName("content")
    val content: String,
    @SerialName("created_at")
    val createdAt: String
)
