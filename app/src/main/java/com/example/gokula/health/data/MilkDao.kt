package com.example.gokula.health.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MilkDao {

    // ✅ Get all entries
    @Query("SELECT * FROM milk_entries WHERE cowId = :cowId ORDER BY date DESC")
    fun forCow(cowId: Long): Flow<List<MilkEntry>>

    // ✅ Get single entry (for update logic)
    @Query("SELECT * FROM milk_entries WHERE cowId = :cowId AND date = :date LIMIT 1")
    suspend fun getByCowAndDate(cowId: Long, date: Long): MilkEntry?

    // ✅ Monthly average (FIXED)
    @Query("""
        SELECT AVG(morningLitres + eveningLitres)
        FROM milk_entries
        WHERE cowId = :cowId AND date BETWEEN :start AND :end
    """)
    fun monthlyAverage(cowId: Long, start: Long, end: Long): Flow<Double?>

    // ✅ Insert
    @Insert
    suspend fun insert(entry: MilkEntry): Long

    // ✅ Delete
    @Delete
    suspend fun delete(entry: MilkEntry)

    // ✅ Update
    @Update
    suspend fun update(entry: MilkEntry)
}