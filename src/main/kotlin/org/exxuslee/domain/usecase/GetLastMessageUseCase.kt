package org.exxuslee.domain.usecase

import org.exxuslee.domain.repository.TelegramRepository

class GetLastMessageUseCase(
    private val repository: TelegramRepository
) {
    suspend operator fun invoke(chatId: Long): String? = repository.getLastMessage(chatId)
}


