package org.exxuslee.domain.usecase

import org.exxuslee.domain.repository.TelegramRepository

class SubscribeToChatUseCase(
    private val repository: TelegramRepository
) {
    suspend operator fun invoke(chatId: Long): Result<Unit> {
        return try {
            // Проверяем, что чат существует и доступен
            val chat = repository.getChat(chatId)
            println("Successfully subscribed to chat: ${chat.title} (ID: $chatId)")
            Result.success(Unit)
        } catch (e: Exception) {
            println("Failed to subscribe to chat $chatId: ${e.message}")
            Result.failure(e)
        }
    }
}
