package org.exxuslee.data.tdlight

import it.tdlight.Init
import it.tdlight.client.*
import io.github.cdimascio.dotenv.Dotenv
import it.tdlight.jni.TdApi
import java.nio.file.Paths

class TdLightClientProvider {
    val client: SimpleTelegramClient by lazy { initializeClient() }

    private val apiId: String
    private val apiHash: String
    private val apiUserId: String

    init {
        val dotenv = Dotenv.configure().ignoreIfMissing().load()
        apiId = getRequiredEnv("API_ID", dotenv)
        apiHash = getRequiredEnv("API_HASH", dotenv)
        apiUserId = getRequiredEnv("API_USER_ID", dotenv)
    }

    private fun getRequiredEnv(key: String, dotenv: Dotenv): String {
        val fromSystem = System.getenv(key)
        val value = fromSystem ?: dotenv.get(key)
        return value ?: throw IllegalStateException(
            "Missing required environment variable '$key'. Create a .env file (or set env var) with $key."
        )
    }

    private fun initializeClient(): SimpleTelegramClient {
        Init.init()
        val clientFactory = SimpleTelegramClientFactory()
        val apiToken = APIToken(apiId.toInt(), apiHash)
        val sessionPath = Paths.get("tdlib-session-id$apiUserId")
        val settings = TDLibSettings.create(apiToken).also {
            it.databaseDirectoryPath = sessionPath.resolve("data")
            it.downloadedFilesDirectoryPath = sessionPath.resolve("downloads")
        }
        val clientBuilder = clientFactory.builder(settings)
        val authenticationData = AuthenticationSupplier.consoleLogin()
        val client = clientBuilder.build(authenticationData)
        client.send(TdApi.SetLogVerbosityLevel(0))
        return client
    }
}



