package org.exxuslee.data.tdlight

import it.tdlight.client.SimpleTelegramClient
import it.tdlight.jni.TdApi
import org.exxuslee.common.removePrefixFromLink
import org.exxuslee.domain.model.Chat
import org.exxuslee.domain.repository.TelegramRepository

class TdLightTelegramRepository(
    private val client: SimpleTelegramClient
) : TelegramRepository {

    override suspend fun getSubscribedChannelIds(): List<Long> {
        return client.send(TdApi.GetChats(null, 100)).get().chatIds.toList().filter { it != 777000L }
    }

    override suspend fun getChat(chatId: Long): Chat {
        val chat = client.send(TdApi.GetChat(chatId)).get()
        val title = when (val info = chat.type) {
            is TdApi.ChatTypeSupergroup -> chat.title
            is TdApi.ChatTypeBasicGroup -> chat.title
            is TdApi.ChatTypePrivate -> chat.title
            is TdApi.ChatTypeSecret -> chat.title
            else -> chat.title
        }
        return Chat(id = chat.id, title = title)
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
}


