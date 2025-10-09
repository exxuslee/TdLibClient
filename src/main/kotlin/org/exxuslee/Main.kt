package org.exxuslee

import kotlinx.coroutines.runBlocking
import org.exxuslee.application.TelegramBotApplication

fun main() {
    runBlocking {
        val application = TelegramBotApplication()
        val targetChatId = -1001921446920L
//        val targetChatId = 5054256299

        try {
            application.start(targetChatId)
        } catch (e: Exception) {
            println("Application error: ${e.message}")
        } finally {
            application.stop()
        }
    }
}