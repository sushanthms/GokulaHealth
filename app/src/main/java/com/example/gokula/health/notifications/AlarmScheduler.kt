package com.example.gokula.health.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.gokula.health.data.Vaccination

object AlarmScheduler {

    private fun pendingIntent(ctx: Context, v: Vaccination): PendingIntent {
        val intent = Intent(ctx, VaccinationAlarmReceiver::class.java).apply {
            putExtra("vaccinationId", v.id)
            putExtra("vaccineName", v.vaccineName)
        }
        return PendingIntent.getBroadcast(
            ctx, v.id.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun schedule(ctx: Context, v: Vaccination) {
        val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = pendingIntent(ctx, v)
        try {
            am.set(
                AlarmManager.RTC_WAKEUP,
                v.scheduledAt,
                pi
            )
        } catch (se: SecurityException) {
            // Fallback if exact alarms not permitted
            am.set(AlarmManager.RTC_WAKEUP, v.scheduledAt, pi)
        }
    }

    fun cancel(ctx: Context, vaccinationId: Long) {
        val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(ctx, VaccinationAlarmReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            ctx, vaccinationId.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        am.cancel(pi)
    }
}
