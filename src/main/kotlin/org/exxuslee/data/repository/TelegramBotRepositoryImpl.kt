package org.exxuslee.data.repository

import io.github.cdimascio.dotenv.Dotenv
import org.exxuslee.common.getRequiredEnv
import org.exxuslee.data.network.TelegramBotService
import org.exxuslee.domain.repository.TelegramBotRepository

class TelegramBotRepositoryImpl(
    private val telegramBotService: TelegramBotService
) : TelegramBotRepository {

    private val channelChatId: Long

    init {
        val dotenv = Dotenv.configure().ignoreIfMissing().load()
        channelChatId = getRequiredEnv("CHANNEL_CHAT_ID", dotenv).toLong()
    }


    override suspend fun sendMessage(text: String) {
        telegramBotService.sendMessage(text, channelChatId)
    }

}