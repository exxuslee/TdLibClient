package org.exxuslee.data.network

import io.github.cdimascio.dotenv.Dotenv
import org.exxuslee.common.getRequiredEnv
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto
import java.io.File

interface TelegramBotService {
    suspend fun sendMessage(text: String, chatId: Long)
    suspend fun sendPhoto(file: File, chatId: Long)
    suspend fun sendPhotoWithCaption(file: ByteArray, caption: String, chatId: Long)
    suspend fun sendPhotoWithCaption(photoUrl: String, caption: String, chatId: Long)
    suspend fun sendScreenshots(files: List<ByteArray>, caption: String, chatId: Long)

    class Base() : TelegramBotService {
        private val dotenv = Dotenv.configure().ignoreIfMissing().load()
        private val token = getRequiredEnv("TELEGRAM_TOKEN", dotenv)
        private val client = OkHttpTelegramClient(token)

        override suspend fun sendMessage(text: String, chatId: Long) {
            if (text.isBlank()) return
            val msg = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .disableWebPagePreview(true)
                .build()
            try {
                val response = client.execute(msg)
                println("✅ sendMessage: ${response.messageId}")
            } catch (e: Exception) {
                println("❌ sendMessage: ${e.message}")
                e.printStackTrace()
            }
        }

        override suspend fun sendPhoto(file: File, chatId: Long) {
            val msg = SendPhoto.builder()
                .chatId(chatId.toString())
                .photo(InputFile(file))
                .build()
            client.execute(msg)
        }

        override suspend fun sendPhotoWithCaption(file: ByteArray, caption: String, chatId: Long) {
            val tmpFile = File.createTempFile("photo", ".jpg").apply { writeBytes(file) }
            val msg = SendPhoto.builder()
                .chatId(chatId.toString())
                .photo(InputFile(tmpFile))
                .caption(caption)
                .build()
            client.execute(msg)
        }

        override suspend fun sendPhotoWithCaption(photoUrl: String, caption: String, chatId: Long) {
            val msg = SendPhoto.builder()
                .chatId(chatId.toString())
                .photo(InputFile(photoUrl)) // URL тоже работает
                .caption(caption)
                .build()
            val response = client.execute(msg)
            println("✅ sendPhotoWithCaption: ${response.messageId}")
        }

        override suspend fun sendScreenshots(files: List<ByteArray>, caption: String, chatId: Long) {
            val media = files.mapIndexed { index, file ->
                val tmpFile = File.createTempFile("screenshot$index", ".jpg").apply { writeBytes(file) }
                InputMediaPhoto.builder()
                    .media(tmpFile, "screenshot$index.jpg")
                    .apply { if (index == 0) caption(caption) }
                    .build()
            }

            val msg = SendMediaGroup.builder()
                .chatId(chatId.toString())
                .medias(media)
                .build()

            val response = client.execute(msg)
            println("✅ sendMediaGroup: ${response.size} messages sent")
        }
    }
}