package com.example.gokula.health.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "farmers",
    indices = [Index(value = ["username"], unique = true)]
)
data class Farmer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val fullName: String,
    val passwordHash: String,  // SHA-256(salt + password)
    val salt: String,
    val createdAt: Long = System.currentTimeMillis()
)
