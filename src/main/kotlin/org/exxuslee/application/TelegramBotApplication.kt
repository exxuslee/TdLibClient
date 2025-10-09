package org.exxuslee.application

import kotlinx.coroutines.delay
import org.exxuslee.data.network.TelegramBotService
import org.exxuslee.data.repository.LocalRepositoryImpl
import org.exxuslee.data.repository.TelegramBotRepositoryImpl
import org.exxuslee.data.tdlight.TdLightClientProvider
import org.exxuslee.data.repository.TelegramClientRepositoryImpl
import org.exxuslee.domain.service.MessageSubscriptionService
import org.exxuslee.domain.usecase.ProcessNewMessageUseCase
import org.exxuslee.domain.usecase.SubscribeToChatUseCase

class TelegramBotApplication {

    private val clientProvider = TdLightClientProvider()
    private val botService = TelegramBotService.Base()
    private val tgClientRepository = TelegramClientRepositoryImpl(clientProvider.client)
    private val tgBotRepository = TelegramBotRepositoryImpl(botService)
    private val localRepository = LocalRepositoryImpl()

    private val processNewMessageUseCase = ProcessNewMessageUseCase(tgBotRepository, localRepository)
    private val subscribeToChatUseCase = SubscribeToChatUseCase(tgClientRepository)

    private val messageSubscriptionService = MessageSubscriptionService(
        repository = tgClientRepository,
        processNewMessageUseCase = processNewMessageUseCase,
        subscribeToChatUseCase = subscribeToChatUseCase
    )

    suspend fun start(targetChatId: Long) {
        println("Client initialized and authenticated.")
        messageSubscriptionService.subscribeToChat(targetChatId)
            .onSuccess {
                println("Successfully subscribed to chat $targetChatId")
            }
            .onFailure { error ->
                println("Failed to subscribe to chat $targetChatId: ${error.message}")
            }
        println("Press Ctrl+C to stop.")
        while (true) {
            delay(15000)
        }
    }

    fun stop() {
        messageSubscriptionService.unsubscribeFromChat()
    }
}
