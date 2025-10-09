package org.exxuslee.domain.usecase

import org.exxuslee.domain.repository.TelegramClientRepository

class SearchAndEnsureJoinedUseCase(
    private val repository: TelegramClientRepository
) {
    suspend operator fun invoke(linkOrUsername: String, limit: Int): List<String> {
        val chat = repository.searchPublicChat(linkOrUsername)
        val history = repository.getHistory(chat.id, limit)
        if (history.size == 1 && limit > 1) {
            repository.joinChat(chat.id)
            return repository.getHistory(chat.id, limit)
        }
        return history
    }
}



