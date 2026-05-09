package com.example.gokula.health.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "milk_entries")
data class MilkEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cowId: Long,
    val date: Long,          // millis (start of day)
    val morningLitres: Double,
    val eveningLitres: Double
) {
    val totalLitres: Double get() = morningLitres + eveningLitres
}
