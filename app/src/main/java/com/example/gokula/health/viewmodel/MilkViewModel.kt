package com.example.gokula.health.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gokula.health.data.MilkDao
import com.example.gokula.health.data.MilkEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Calendar

class MilkViewModel(private val dao: MilkDao) : ViewModel() {

    fun entries(cowId: Long): Flow<List<MilkEntry>> = dao.forCow(cowId)

    fun monthlyAverage(cowId: Long): Flow<Double?> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        val start = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        val end = cal.timeInMillis - 1

        return dao.monthlyAverage(cowId, start, end)
    }

    fun add(entry: MilkEntry) {
        viewModelScope.launch {

            val existing = dao.getByCowAndDate(entry.cowId, entry.date)

            if (existing != null) {

                // ✅ ONLY UPDATE IF VALUE IS PROVIDED
                val morning = if (entry.morningLitres >= 0)
                    entry.morningLitres
                else
                    existing.morningLitres

                val evening = if (entry.eveningLitres >= 0)
                    entry.eveningLitres
                else
                    existing.eveningLitres

                dao.update(
                    existing.copy(
                        morningLitres = morning,
                        eveningLitres = evening
                    )
                )

            } else {
                dao.insert(entry)
            }
        }
    }

    fun delete(entry: MilkEntry) =
        viewModelScope.launch { dao.delete(entry) }
}