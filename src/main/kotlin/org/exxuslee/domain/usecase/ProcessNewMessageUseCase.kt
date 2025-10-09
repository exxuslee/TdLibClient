package org.exxuslee.domain.usecase

import org.exxuslee.domain.model.ArkhamMessage
import org.exxuslee.domain.model.LocalData
import org.exxuslee.domain.model.Message
import org.exxuslee.domain.repository.LocalRepository
import org.exxuslee.domain.repository.TelegramBotRepository
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

class ProcessNewMessageUseCase(
    private val telegramBotRepository: TelegramBotRepository,
    private val localRepository: LocalRepository,
) {
    suspend operator fun invoke(message: Message): Result<Unit> {
        return try {
            val arkhamMessage = parseMessage(message)
            println(arkhamMessage)
            val posts = localRepository.readLocalDataFromFile().posts
            val updatedPosts = deleteOldPost(posts) + arkhamMessage
            localRepository.saveLocalDataToFile(LocalData(updatedPosts))

            var inCex = 0.0
            var outCex = 0.0

            updatedPosts.forEach { post ->
                val isFromCEX = isCEX(post.from)
                val isToCEX = isCEX(post.to)
                val amount = parseAmount(post.value) ?: return@forEach
                val coefficient = coefficient(post.timestamp)

                when {
                    isFromCEX && !isToCEX -> outCex += amount * coefficient
                    !isFromCEX && isToCEX -> inCex -= amount * coefficient
                }

            }

            val count = (outCex - inCex).toLong()
            val ruLocale = Locale.Builder().setLanguage("ru").setRegion("RU").build()
            val countFormatted = NumberFormat.getNumberInstance(ruLocale).format(count)
            val div = if (outCex != 0.0 && inCex != 0.0) "%.1f".format(outCex / inCex) else "N/A"
            val iconCount = when {
                count > 0 -> "🚀"
                count < 0 -> "🔻"
                else -> "⚪️"
            }
            val iconDiv = when {
                (div.toDoubleOrNull() ?: 1.0) > 1 -> "📈"
                (div.toDoubleOrNull() ?: 1.0) < 1 -> "📉"
                else -> "⚪️"
            }

            val push = "${iconDiv}${iconCount} | $div | $countFormatted"
            println("$push\n")
            telegramBotRepository.sendMessage(push)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun coefficient(timestamp: Long): Double {
        val now = System.currentTimeMillis()
        val twoWeekMillis = TimeUnit.DAYS.toMillis(14)

        val age = now - timestamp

        return when {
            age <= 0 -> 1.0
            age >= twoWeekMillis -> 0.0
            else -> {
                val ratio = 1.0 - (age.toDouble() / twoWeekMillis.toDouble())
                min(1.0, max(0.0, ratio))
            }
        }
    }

    private fun deleteOldPost(posts: List<ArkhamMessage>): List<ArkhamMessage> {
        val twoWeekMillis = TimeUnit.DAYS.toMillis(14)
        val now = System.currentTimeMillis()

        return posts.filter { msg ->
            now - msg.timestamp <= twoWeekMillis
        }
    }

    private fun parseMessage(message: Message): ArkhamMessage {
        val lines = message.content.lines()
        val from = lines.find { it.startsWith("From:") }?.removePrefix("From:")?.trim() ?: ""
        val to = lines.find { it.startsWith("To:") }?.removePrefix("To:")?.trim() ?: ""
        val value = lines.find { it.startsWith("Value:") }?.removePrefix("Value:")?.trim() ?: ""
        val network = lines.find { it.startsWith("Network:") }?.removePrefix("Network:")?.trim() ?: ""
        val time = lines.find { it.startsWith("Time:") }?.removePrefix("Time:")?.trim() ?: ""
        return ArkhamMessage(from, to, value, network, parseTime(time))
    }

    private fun isCEX(address: String): Boolean {
        val keywords = listOf(
            "Hot Wallet",
            "Cold Wallet",
            "Gemini",
            "Bitfinex",
            "Bybit",
            "Binance",
            "Kraken",
            "Coinbase",
            "FTX",
            "Kucoin",
            "Crypto.com",
            "Bitstamp",
            "Bittrex",
            "Poloniex",
            "OKX",
            "Huobi",
            "B2C2",
            "Robinhood",
            "Ceffu",
        )
        return keywords.any { keyword -> address.contains(keyword, ignoreCase = true) }
    }

    private fun parseAmount(line: String): Long? {
        val regex = """[\d,\.]+\s+([A-Za-z ]+)\s+\(\$(.*?)\)""".toRegex()
        val match = regex.find(line) ?: return null
        val tickerRaw = match.groupValues[1].trim()
        val usdStr = match.groupValues[2].replace(",", "")
        val usdValue = usdStr.toDouble().toLong()

        val ticker = when {
            tickerRaw.contains("Tether", ignoreCase = true) ||
                    tickerRaw.contains("USDT", ignoreCase = true) -> "USDT"

            tickerRaw.contains("USDC", ignoreCase = true) -> "USDC"
            tickerRaw.contains("ETH", ignoreCase = true) -> "ETH"
            tickerRaw.contains("BTC", ignoreCase = true) -> "BTC"
            else -> tickerRaw.uppercase()
        }

        return when (ticker) {
            "USDT", "USDC" -> -usdValue
            else -> usdValue
        }
    }

    private fun parseTime(line: String): Long {
        // Example line: "Time: 2024-06-20 12:34:56 UTC"
        val regex = """(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}) UTC""".toRegex()
        val match = regex.find(line) ?: return 0L
        val dateTimeStr = match.groupValues[1]
        val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val localDateTime = java.time.LocalDateTime.parse(dateTimeStr, formatter)
        val zonedDateTime = localDateTime.atZone(java.time.ZoneOffset.UTC)
        return zonedDateTime.toInstant().toEpochMilli()
    }
}
