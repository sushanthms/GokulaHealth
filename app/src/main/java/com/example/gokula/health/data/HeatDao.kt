package com.example.gokula.health.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HeatDao {

    @Query("SELECT * FROM heat_cycles ORDER BY lastHeatDate DESC")
    fun getAll(): Flow<List<HeatCycle>>

    @Insert
    suspend fun insert(h: HeatCycle): Long

    @Delete
    suspend fun delete(h: HeatCycle)
}