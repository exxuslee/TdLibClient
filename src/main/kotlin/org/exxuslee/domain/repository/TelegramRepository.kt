package org.exxuslee.domain.repository

import org.exxuslee.domain.model.Chat

interface TelegramRepository {
    suspend fun getSubscribedChannelIds(): List<Long>
    suspend fun getChat(chatId: Long): Chat
    suspend fun getLastMessage(chatId: Long): String?
    suspend fun getHistory(chatId: Long, limit: Int): List<String>
    suspend fun searchPublicChat(usernameOrLink: String): Chat
    suspend fun joinChat(chatId: Long)
}


