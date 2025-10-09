package org.exxuslee.domain.repository


interface TelegramBotRepository {

    suspend fun sendMessage(text: String,)

}



