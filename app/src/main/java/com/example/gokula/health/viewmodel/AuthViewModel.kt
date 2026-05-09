package com.example.gokula.health.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gokula.health.data.Farmer
import com.example.gokula.health.data.FarmerDao
import com.example.gokula.health.util.PasswordUtil
import kotlinx.coroutines.launch

class AuthViewModel(private val dao: FarmerDao) : ViewModel() {

    fun signup(
        username: String,
        fullName: String,
        password: String,
        onResult: (success: Boolean, farmerId: Long?, error: String?) -> Unit
    ) {
        viewModelScope.launch {
            val u = username.trim().lowercase()
            if (u.length < 3) { onResult(false, null, "Username must be 3+ chars"); return@launch }
            if (password.length < 4) { onResult(false, null, "Password must be 4+ chars"); return@launch }
            if (dao.countByUsername(u) > 0) { onResult(false, null, "Username already exists"); return@launch }

            val salt = PasswordUtil.newSalt()
            val hash = PasswordUtil.hash(password, salt)
            val id = dao.insert(Farmer(username = u, fullName = fullName.trim(), passwordHash = hash, salt = salt))
            onResult(true, id, null)
        }
    }

    fun login(
        username: String,
        password: String,
        onResult: (success: Boolean, farmer: Farmer?, error: String?) -> Unit
    ) {
        viewModelScope.launch {
            val u = username.trim().lowercase()
            val farmer = dao.findByUsername(u)
            if (farmer == null) { onResult(false, null, "User not found"); return@launch }
            if (!PasswordUtil.verify(password, farmer.salt, farmer.passwordHash)) {
                onResult(false, null, "Wrong password"); return@launch
            }
            onResult(true, farmer, null)
        }
    }
}
