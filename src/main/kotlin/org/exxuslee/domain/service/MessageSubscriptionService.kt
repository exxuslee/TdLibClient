package org.exxuslee.domain.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.exxuslee.domain.repository.TelegramClientRepository
import org.exxuslee.domain.usecase.ProcessNewMessageUseCase
import org.exxuslee.domain.usecase.SubscribeToChatUseCase

class MessageSubscriptionService(
    private val repository: TelegramClientRepository,
    private val processNewMessageUseCase: ProcessNewMessageUseCase,
    private val subscribeToChatUseCase: SubscribeToChatUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun subscribeToChat(chatId: Long): Result<Unit> {
        return try {
            // Подписываемся на чат
            subscribeToChatUseCase(chatId).getOrThrow()
            
            // Настраиваем обработчик сообщений
            repository.addMessageUpdateHandler { message ->
                if (message.chatId == chatId) {
                    CoroutineScope(Dispatchers.IO).launch {
                        processNewMessageUseCase(message)
                    }
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun unsubscribeFromChat() {
        repository.removeMessageUpdateHandler()
        scope.cancel()
    }
}
