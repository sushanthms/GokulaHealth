package com.example.gokula.health.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gokula.health.data.Cow
import com.example.gokula.health.data.CowDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CowViewModel(private val dao: CowDao) : ViewModel() {
    val cows = dao.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun add(cow: Cow) = viewModelScope.launch { dao.insert(cow) }
    fun delete(cow: Cow) = viewModelScope.launch { dao.delete(cow) }
}
