package com.example.gokula.health.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gokula.health.data.HeatCycle
import com.example.gokula.health.data.HeatDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HeatViewModel(private val dao: HeatDao) : ViewModel() {

    val cycles = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun add(h: HeatCycle) {
        viewModelScope.launch {
            dao.insert(h)
        }
    }

    fun delete(h: HeatCycle) {
        viewModelScope.launch {
            dao.delete(h)
        }
    }
}