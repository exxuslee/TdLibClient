package org.exxuslee.domain.repository

import org.exxuslee.domain.model.LocalData

interface LocalRepository {

    fun readLocalDataFromFile(): LocalData
    suspend fun saveLocalDataToFile(localData: LocalData)
}