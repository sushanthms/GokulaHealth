package com.example.gokula.health.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CowDao {
    @Query("SELECT * FROM cows ORDER BY name")
    fun getAll(): Flow<List<Cow>>

    @Query("SELECT * FROM cows WHERE id = :id")
    suspend fun getById(id: Long): Cow?

    @Insert suspend fun insert(cow: Cow): Long
    @Update suspend fun update(cow: Cow)
    @Delete suspend fun delete(cow: Cow)
}
