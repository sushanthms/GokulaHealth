package com.example.gokula.health.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "heat_cycles")
data class HeatCycle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cowId: Long,
    val lastHeatDate: Long   // millis
) {
    val nextExpected: Long get() = lastHeatDate + 21L * 24 * 60 * 60 * 1000
}
