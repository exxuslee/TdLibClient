package org.exxuslee.domain.usecase

import org.exxuslee.domain.repository.TelegramRepository

class GetChatHistoryUseCase(
    private val repository: TelegramRepository
) {
    suspend operator fun invoke(chatId: Long, limit: Int): List<String> = repository.getHistory(chatId, limit)
}



