package org.exxuslee.domain.model

import java.time.LocalDateTime

data class Message(
    val id: Long,
    val chatId: Long,
    val senderId: Long?,
    val content: String,
    val timestamp: LocalDateTime,
    val messageType: MessageType
)

enum class MessageType {
    TEXT,
    PHOTO,
    VIDEO,
    DOCUMENT,
    AUDIO,
    VOICE,
    STICKER,
    ANIMATION,
    OTHER
}
