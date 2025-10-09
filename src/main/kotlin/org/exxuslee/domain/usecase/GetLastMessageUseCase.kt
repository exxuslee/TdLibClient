package org.exxuslee.domain.usecase

import org.exxuslee.domain.repository.TelegramClientRepository

class GetLastMessageUseCase(
    private val repository: TelegramClientRepository
) {
    suspend operator fun invoke(chatId: Long): String? = repository.getLastMessage(chatId)
}



