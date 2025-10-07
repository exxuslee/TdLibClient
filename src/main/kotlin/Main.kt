package org.exxuslee

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.exxuslee.data.tdlight.TdLightClientProvider
import org.exxuslee.data.tdlight.TdLightTelegramRepository
import org.exxuslee.domain.usecase.GetLastMessageUseCase
import org.exxuslee.domain.usecase.GetSubscribedChannelsUseCase


fun main() {
    println("Starting TDLight client. Follow console prompts to authenticate by phone.")
    runBlocking {
        val clientProvider = TdLightClientProvider()
        val repository = TdLightTelegramRepository(clientProvider.client)

        val getSubscribedChannels = GetSubscribedChannelsUseCase(repository)
        val getLastMessage = GetLastMessageUseCase(repository)
        println("Client initialized and authenticated.")

        val channels = getSubscribedChannels()
        println("Channels: $channels")
        channels.forEach { chatId ->
            val lastMessage = getLastMessage(chatId)
            println("Last message in channel $chatId: $lastMessage")
        }

        while (true) {
            delay(1000)
        }
    }
}