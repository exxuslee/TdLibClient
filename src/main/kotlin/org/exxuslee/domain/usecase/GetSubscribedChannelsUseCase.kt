package org.exxuslee.domain.usecase

import org.exxuslee.domain.repository.TelegramClientRepository

class GetSubscribedChannelsUseCase(
    private val repository: TelegramClientRepository
) {
    suspend operator fun invoke(): List<Long> = repository.getSubscribedChannelIds()
}



