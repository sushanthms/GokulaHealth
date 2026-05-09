package com.example.gokula.health.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vaccinations")
data class Vaccination(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cowId: Long,
    val vaccineName: String,
    val scheduledAt: Long,   // millis
    val done: Boolean = false
)
