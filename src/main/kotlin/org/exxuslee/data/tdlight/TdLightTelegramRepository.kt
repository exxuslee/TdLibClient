package org.exxuslee.data.tdlight

import it.tdlight.client.SimpleTelegramClient
import it.tdlight.jni.TdApi
import org.exxuslee.common.removePrefixFromLink
import org.exxuslee.domain.model.Chat
import org.exxuslee.domain.model.Message
import org.exxuslee.domain.model.MessageType
import org.exxuslee.domain.repository.TelegramRepository
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.function.Consumer

class TdLightTelegramRepository(
    private val client: SimpleTelegramClient
) : TelegramRepository {

    private var messageHandler: Consumer<Message>? = null

    override suspend fun getSubscribedChannelIds(): List<Long> {
        return client.send(TdApi.GetChats(null, 100)).get().chatIds.toList().filter { it != 777000L }
    }

    override suspend fun getChat(chatId: Long): Chat {
        val chat = client.send(TdApi.GetChat(chatId)).get()
        return Chat(id = chat.id, title = chat.title)
    }

    override suspend fun getLastMessage(chatId: Long): String? {
        val chat = client.send(TdApi.GetChat(chatId)).get()
        return chat.lastMessage?.content?.let { content ->
            when (content) {
                is TdApi.MessageText -> content.text.text
                else -> content.javaClass.simpleName
            }
        }
    }

    override suspend fun getHistory(chatId: Long, limit: Int): List<String> {
        val messages = client.send(
            TdApi.GetChatHistory(chatId, 0, 0, limit, false)
        ).get().messages.toList()
        return messages.map { message ->
            when (val c = message.content) {
                is TdApi.MessageText -> c.text.text
                else -> c.javaClass.simpleName
            }
        }
    }

    override suspend fun searchPublicChat(usernameOrLink: String): Chat {
        val chat = client.send(
            TdApi.SearchPublicChat(usernameOrLink.removePrefixFromLink())
        ).get()
        return Chat(id = chat.id, title = chat.title)
    }

    override suspend fun joinChat(chatId: Long) {
        val result = client.send(TdApi.JoinChat(chatId)).get()
        if (result !is TdApi.Ok) {
            throw IllegalStateException("Failed to join chat $chatId")
        }
    }

    override fun addMessageUpdateHandler(handler: Consumer<Message>) {
        messageHandler = handler
        client.addUpdateHandler(TdApi.UpdateNewMessage::class.java) { update ->
            val message = update.message
            val domainMessage = convertToDomainMessage(message)
            handler.accept(domainMessage)
        }
    }

    override fun removeMessageUpdateHandler() {
        messageHandler = null
        // TdLight не предоставляет прямой способ удаления обработчиков
        // В реальном приложении можно использовать более сложную логику
    }

    private fun convertToDomainMessage(tdMessage: TdApi.Message): Message {
        val content = when (val tdContent = tdMessage.content) {
            is TdApi.MessageText -> tdContent.text.text
            is TdApi.MessagePhoto -> "[PHOTO]"
            is TdApi.MessageVideo -> "[VIDEO]"
            is TdApi.MessageDocument -> "[DOCUMENT]"
            is TdApi.MessageAudio -> "[AUDIO]"
            is TdApi.MessageVoiceNote -> "[VOICE]"
            is TdApi.MessageSticker -> "[STICKER]"
            is TdApi.MessageAnimation -> "[ANIMATION]"
            else -> "[${tdContent.javaClass.simpleName}]"
        }

        val messageType = when (tdMessage.content) {
            is TdApi.MessageText -> MessageType.TEXT
            is TdApi.MessagePhoto -> MessageType.PHOTO
            is TdApi.MessageVideo -> MessageType.VIDEO
            is TdApi.MessageDocument -> MessageType.DOCUMENT
            is TdApi.MessageAudio -> MessageType.AUDIO
            is TdApi.MessageVoiceNote -> MessageType.VOICE
            is TdApi.MessageSticker -> MessageType.STICKER
            is TdApi.MessageAnimation -> MessageType.ANIMATION
            else -> MessageType.OTHER
        }

        val timestamp = LocalDateTime.ofEpochSecond(
            tdMessage.date.toLong(),
            0,
            ZoneOffset.UTC
        )

        return Message(
            id = tdMessage.id,
            chatId = tdMessage.chatId,
            senderId = tdMessage.senderId?.let { 
                when (it) {
                    is TdApi.MessageSenderUser -> it.userId
                    is TdApi.MessageSenderChat -> it.chatId
                    else -> null
                }
            },
            content = content,
            timestamp = timestamp,
            messageType = messageType
        )
    }
}



