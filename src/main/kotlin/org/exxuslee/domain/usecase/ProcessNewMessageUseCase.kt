package org.exxuslee.domain.usecase

import org.exxuslee.domain.model.Amount
import org.exxuslee.domain.model.ArkhamMessage
import org.exxuslee.domain.model.Message
import org.exxuslee.domain.repository.TelegramRepository

class ProcessNewMessageUseCase(
    private val repository: TelegramRepository
) {
    operator fun invoke(message: Message): Result<Unit> {
        return try {
            val lines = message.content.lines()
            val from = lines.find { it.startsWith("From:") }?.removePrefix("From: ")?.trim() ?: ""
            val to = lines.find { it.startsWith("To:") }?.removePrefix("To: ")?.trim() ?: ""
            val value = lines.find { it.startsWith("Value:") }?.removePrefix("Value: ")?.trim() ?: ""
            val network = lines.find { it.startsWith("Network:") }?.removePrefix("Network: ")?.trim() ?: ""
            val parsedMessage = ArkhamMessage(from, to, value, network)

            val isFromCEX = isCEX(from)
            val isToCEX = isCEX(to)
            val amount = parseAmount(value)

            println("$isFromCEX $isToCEX $amount")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
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

    fun parseAmount(line: String): Amount? {
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
            "USDT", "USDC" -> Amount.Stable(usdValue)
            else -> Amount.Coin(usdValue)
        }
    }
}
