package org.exxuslee.domain.usecase

import org.exxuslee.domain.repository.TelegramRepository

class GetSubscribedChannelsUseCase(
    private val repository: TelegramRepository
) {
    suspend operator fun invoke(): List<Long> = repository.getSubscribedChannelIds()
}


