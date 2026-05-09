package com.example.gokula.health.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Cow::class,
        MilkEntry::class,
        Vaccination::class,
        HeatCycle::class,
        Farmer::class          // <-- add
    ],
    version = 4,                // <-- bump from 1 to 2
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cowDao(): CowDao
    abstract fun milkDao(): MilkDao
    abstract fun vaccinationDao(): VaccinationDao
    abstract fun heatDao(): HeatDao
    abstract fun farmerDao(): FarmerDao   // <-- add

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: android.content.Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gokula.db"
                )
                    .fallbackToDestructiveMigration()   // <-- ok for dev; wipes old data
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
