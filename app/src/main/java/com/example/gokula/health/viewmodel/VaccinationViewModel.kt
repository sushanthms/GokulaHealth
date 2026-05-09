package com.example.gokula.health.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gokula.health.data.Vaccination
import com.example.gokula.health.data.VaccinationDao
import com.example.gokula.health.notifications.AlarmScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VaccinationViewModel(
    private val dao: VaccinationDao
) : ViewModel() {

    val items = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun getByCow(cowId: Long) = dao.getByCow(cowId)

    fun schedule(v: Vaccination, context: Context) {
        viewModelScope.launch {

            val id = dao.insert(v)

            val updated = v.copy(id = id)

            // ✅ USE AlarmScheduler ONLY
            AlarmScheduler.schedule(context, updated)
        }
    }

    fun markDone(v: Vaccination) {
        viewModelScope.launch {
            dao.update(v.copy(done = true))
        }
    }

    fun delete(v: Vaccination) {
        viewModelScope.launch {
            dao.delete(v)
        }
    }
}