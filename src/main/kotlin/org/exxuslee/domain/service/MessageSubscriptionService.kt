package org.exxuslee.domain.service

import org.exxuslee.domain.repository.TelegramRepository
import org.exxuslee.domain.usecase.ProcessNewMessageUseCase
import org.exxuslee.domain.usecase.SubscribeToChatUseCase

class MessageSubscriptionService(
    private val repository: TelegramRepository,
    private val processNewMessageUseCase: ProcessNewMessageUseCase,
    private val subscribeToChatUseCase: SubscribeToChatUseCase
) {
    
    suspend fun subscribeToChat(chatId: Long): Result<Unit> {
        return try {
            // Подписываемся на чат
            subscribeToChatUseCase(chatId).getOrThrow()
            
            // Настраиваем обработчик сообщений
            repository.addMessageUpdateHandler { message ->
                if (message.chatId == chatId) {
                    processNewMessageUseCase(message)
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun unsubscribeFromChat() {
        repository.removeMessageUpdateHandler()
    }
}
