package org.exxuslee.data.repository

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.exxuslee.domain.model.LocalData
import org.exxuslee.domain.repository.LocalRepository
import java.io.File

class LocalRepositoryImpl : LocalRepository {

    companion object {
        private const val PATH = "count.json"
    }

    override fun readLocalDataFromFile(): LocalData {
        val json = File(PATH).readText()
        return Json.Default.decodeFromString<LocalData>(json)
    }

    override fun saveLocalDataToFile(localData: LocalData) {
        val json = Json.Default.encodeToString(localData)
        File(PATH).writeText(json)
    }
}