package org.exxuslee.data.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.exxuslee.domain.model.LocalData
import org.exxuslee.domain.repository.LocalRepository
import java.io.File



class LocalRepositoryImpl : LocalRepository {
    private val json = Json { prettyPrint = true }
    private val fileMutex = Mutex()

    companion object {
        private const val PATH = "count.json"
    }

    override fun readLocalDataFromFile(): LocalData {
        val file = File(PATH)
        if (!file.exists()) return LocalData(emptyList())
        val json = file.readText()
        return Json.decodeFromString<LocalData>(json)
    }

    override suspend fun saveLocalDataToFile(localData: LocalData) {
        val json = json.encodeToString(localData)
        fileMutex.withLock {
            File(PATH).writeText(json)
        }
    }
}