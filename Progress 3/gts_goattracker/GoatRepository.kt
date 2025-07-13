package com.example.gts_goattracker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoatRepository(private val goatDao: GoatDao) {

    suspend fun insertGoat(goat: Goat) = withContext(Dispatchers.IO) {
        goatDao.insert(goat)
    }

    suspend fun getAllGoats(): List<Goat> = withContext(Dispatchers.IO) {
        goatDao.getAll()
    }

    suspend fun getGoatById(id: Int): Goat? = withContext(Dispatchers.IO) {
        goatDao.getById(id)
    }

    suspend fun updateGoat(goat: Goat) = withContext(Dispatchers.IO) {
        goatDao.update(goat)
    }

    suspend fun deleteGoat(goat: Goat) = withContext(Dispatchers.IO) {
        goatDao.delete(goat)
    }
}