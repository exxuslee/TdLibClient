package org.exxuslee.data.repository

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.exxuslee.domain.model.LocalData
import org.exxuslee.domain.repository.LocalRepository
import java.io.File



class LocalRepositoryImpl : LocalRepository {
    private val json = Json { prettyPrint = true }

    companion object {
        private const val PATH = "count.json"
    }

    override fun readLocalDataFromFile(): LocalData {
        val file = File(PATH)
        if (!file.exists()) return LocalData(emptyList())
        val json = file.readText()
        return Json.decodeFromString<LocalData>(json)
    }

    override fun saveLocalDataToFile(localData: LocalData) {
        val json = json.encodeToString(localData)
        File(PATH).writeText(json)
    }
}