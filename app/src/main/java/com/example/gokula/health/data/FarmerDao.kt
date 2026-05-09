package com.example.gokula.health.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FarmerDao {
    @Insert
    suspend fun insert(farmer: Farmer): Long

    @Query("SELECT * FROM farmers WHERE username = :username LIMIT 1")
    suspend fun findByUsername(username: String): Farmer?

    @Query("SELECT COUNT(*) FROM farmers WHERE username = :username")
    suspend fun countByUsername(username: String): Int
}
