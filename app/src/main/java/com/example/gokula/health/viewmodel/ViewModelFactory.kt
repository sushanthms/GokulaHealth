package com.example.gokula.health.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gokula.health.GokulaApp

class GokulaViewModelFactory(private val app: Application) : ViewModelProvider.Factory {

    private val db get() = (app as GokulaApp).database

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {

        modelClass.isAssignableFrom(CowViewModel::class.java) ->
            CowViewModel(db.cowDao()) as T

        modelClass.isAssignableFrom(MilkViewModel::class.java) ->
            MilkViewModel(db.milkDao()) as T

        modelClass.isAssignableFrom(VaccinationViewModel::class.java) ->
            VaccinationViewModel(db.vaccinationDao()) as T   // ✅ FIXED

        modelClass.isAssignableFrom(HeatViewModel::class.java) ->
            HeatViewModel(db.heatDao()) as T

        modelClass.isAssignableFrom(AuthViewModel::class.java) ->
            AuthViewModel(db.farmerDao()) as T

        else -> throw IllegalArgumentException("Unknown VM: $modelClass")
    }
}