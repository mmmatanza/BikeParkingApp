package es.ubu.bikeparkingapp.data.mapper

import es.ubu.bikeparkingapp.data.dto.ChatMessageDto
import es.ubu.bikeparkingapp.domain.entity.ChatMessage
import es.ubu.bikeparkingapp.domain.entity.MessageRole
import kotlin.time.Instant

fun ChatMessageDto.toDomain(): ChatMessage {
    return ChatMessage(
        messageId = messageId,
        accountId = accountId,
        role = MessageRole.valueOf(role),
        content = content,
        createdAt = Instant.parse(createdAt)
    )
}
