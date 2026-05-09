package com.example.gokula.health

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.gokula.health.data.AppDatabase

class GokulaApp : Application() {

    val database by lazy { AppDatabase.getInstance(this) }

    // ✅ Make nullable (important)
    var currentFarmerId: Long? = null
    var currentFarmerName: String? = null

    fun logout() {
        currentFarmerId = null
        currentFarmerName = null

        // also clear saved login
        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        loadLogin()   // ✅ load saved user
    }

    fun saveLogin(farmerId: Long, name: String) {
        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        prefs.edit()
            .putLong("id", farmerId)
            .putString("name", name)
            .apply()
    }

    fun loadLogin() {
        val prefs = getSharedPreferences("user", MODE_PRIVATE)

        val id = prefs.getLong("id", -1)
        val name = prefs.getString("name", null)

        currentFarmerId = if (id != -1L) id else null
        currentFarmerName = name
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Vaccination Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for cattle vaccinations"
            }

            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "vaccination_channel"
    }
}