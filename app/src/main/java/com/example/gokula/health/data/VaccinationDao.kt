package com.example.gokula.health.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VaccinationDao {

    // ✅ Get all vaccinations
    @Query("SELECT * FROM vaccinations ORDER BY scheduledAt DESC")
    fun getAll(): Flow<List<Vaccination>>

    // ✅ Get vaccinations for selected cow
    @Query("SELECT * FROM vaccinations WHERE cowId = :cowId ORDER BY scheduledAt DESC")
    fun getByCow(cowId: Long): Flow<List<Vaccination>>

    // ✅ Insert
    @Insert
    suspend fun insert(v: Vaccination): Long

    // ✅ Update
    @Update
    suspend fun update(v: Vaccination)

    // ✅ Delete
    @Delete
    suspend fun delete(v: Vaccination)
}