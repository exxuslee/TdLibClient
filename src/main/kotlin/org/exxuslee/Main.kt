package org.exxuslee

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.exxuslee.application.TelegramBotApplication
import java.net.HttpURLConnection
import java.net.URL

fun main() {
    println("Application starting...")
    runBlocking {
        while (!isInternetAccessible()) delay(120_000)
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

private suspend fun isInternetAccessible(): Boolean = withContext(Dispatchers.IO) {
    try {
        val url = URL("https://google.com")
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 2000
        connection.connect()
        connection.responseCode == 200
    } catch (e: Exception) {
        println("Internet access check failed: ${e.message}")
        false
    }
}