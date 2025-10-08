package org.exxuslee.application

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.exxuslee.data.tdlight.TdLightClientProvider
import org.exxuslee.data.tdlight.TdLightTelegramRepository
import org.exxuslee.domain.service.MessageSubscriptionService
import org.exxuslee.domain.usecase.GetChatHistoryUseCase
import org.exxuslee.domain.usecase.ProcessNewMessageUseCase
import org.exxuslee.domain.usecase.SubscribeToChatUseCase

class TelegramBotApplication {
    
    private val clientProvider = TdLightClientProvider()
    private val repository = TdLightTelegramRepository(clientProvider.client)
    
    private val getChatHistoryUseCase = GetChatHistoryUseCase(repository)
    private val processNewMessageUseCase = ProcessNewMessageUseCase(repository)
    private val subscribeToChatUseCase = SubscribeToChatUseCase(repository)
    
    private val messageSubscriptionService = MessageSubscriptionService(
        repository = repository,
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
