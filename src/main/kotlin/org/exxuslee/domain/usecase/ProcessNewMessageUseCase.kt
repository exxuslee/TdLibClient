package org.exxuslee.domain.usecase

import org.exxuslee.domain.model.ArkhamMessage
import org.exxuslee.domain.model.Message
import org.exxuslee.domain.repository.TelegramRepository

class ProcessNewMessageUseCase(
    private val repository: TelegramRepository
) {
    operator fun invoke(message: Message): Result<Unit> {
        return try {
            val lines = message.content.lines()
            val from = lines.find { it.startsWith("From:") }?.removePrefix("From:")?.trim() ?: ""
            val to = lines.find { it.startsWith("To:") }?.removePrefix("To:")?.trim() ?: ""
            val value = lines.find { it.startsWith("Value:") }?.removePrefix("Value:")?.trim() ?: ""
            val network = lines.find { it.startsWith("Network:") }?.removePrefix("Network:")?.trim() ?: ""
            val parsedMessage = ArkhamMessage(from, to, value, network)

            println(parsedMessage)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
