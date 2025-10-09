package org.exxuslee.domain.usecase

import org.exxuslee.domain.repository.TelegramClientRepository

class SubscribeToChatUseCase(
    private val repository: TelegramClientRepository
) {
    suspend operator fun invoke(chatId: Long): Result<Unit> {
        return try {
            val chat = repository.getChat(chatId)
            println("Successfully subscribed to chat: ${chat.title} (ID: $chatId)")
            Result.success(Unit)
        } catch (e: Exception) {
            println("Failed to subscribe to chat $chatId: ${e.message}")
            Result.failure(e)
        }
    }
}
