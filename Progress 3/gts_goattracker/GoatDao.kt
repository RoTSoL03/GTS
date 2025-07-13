package com.example.gts_goattracker

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GoatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goat: Goat)

    @Query("SELECT * FROM goats WHERE id = :id")
    suspend fun getById(id: Int): Goat?

    @Query("SELECT * FROM goats ORDER BY dateRegistered DESC")
    fun getAllFlow(): Flow<List<Goat>>

    @Query("SELECT * FROM goats ORDER BY dateRegistered DESC")
    suspend fun getAll(): List<Goat>

    @Query("SELECT * FROM goats WHERE name LIKE '%' || :searchQuery || '%' OR tagId LIKE '%' || :searchQuery || '%'")
    suspend fun searchGoats(searchQuery: String): List<Goat>

    @Delete
    suspend fun delete(goat: Goat)

    @Update
    suspend fun update(goat: Goat)

    @Query("SELECT COUNT(*) FROM goats")
    suspend fun getGoatCount(): Int
}