package org.exxuslee.domain.usecase

import org.exxuslee.domain.repository.TelegramClientRepository

class GetChatHistoryUseCase(
    private val repository: TelegramClientRepository
) {
    suspend operator fun invoke(chatId: Long, limit: Int): List<String> = repository.getHistory(chatId, limit)
}



