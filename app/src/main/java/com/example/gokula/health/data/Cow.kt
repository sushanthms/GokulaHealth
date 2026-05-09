package com.example.gokula.health.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cows")
data class Cow(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val earTagId: String,
    val name: String,
    val breed: String,
    val dateOfBirth: Long,   // millis
    val photoUri: String? = null
)
