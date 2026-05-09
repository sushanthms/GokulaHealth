package com.example.gokula.health

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.gokula.health.viewmodel.AuthViewModel
import com.example.gokula.health.viewmodel.GokulaViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var vm: AuthViewModel
    private var isSignup = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Auto-login if already saved
        val app = application as GokulaApp
        if (app.currentFarmerId != null) {
            goToMain()
            return
        }

        setContentView(R.layout.activity_login)

        vm = ViewModelProvider(this, GokulaViewModelFactory(application))
            .get(AuthViewModel::class.java)

        val tvMode = findViewById<TextView>(R.id.tvMode)
        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btn = findViewById<Button>(R.id.btnPrimary)
        val tvToggle = findViewById<TextView>(R.id.tvToggle)

        tvToggle.setOnClickListener {
            isSignup = !isSignup
            tvMode.text = if (isSignup) "Create Account" else "Farmer Login"
            btn.text = if (isSignup) "Sign Up" else "Login"
            etFullName.visibility = if (isSignup) View.VISIBLE else View.GONE
            tvToggle.text =
                if (isSignup) "Have an account? Login" else "New user? Create account"
        }

        btn.setOnClickListener {

            val u = etUsername.text.toString()
            val p = etPassword.text.toString()

            if (u.isBlank() || p.isBlank()) {
                Toast.makeText(this, "Enter username & password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isSignup) {

                val name = etFullName.text.toString()

                if (name.isBlank()) {
                    Toast.makeText(this, "Enter full name", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                vm.signup(u, name, p) { ok, id, err ->
                    runOnUiThread {
                        if (ok && id != null) {

                            val app = application as GokulaApp
                            app.currentFarmerId = id
                            app.currentFarmerName = name

                            // ✅ SAVE LOGIN (IMPORTANT)
                            app.saveLogin(id, name)

                            goToMain()

                        } else {
                            Toast.makeText(this, err ?: "Signup failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            } else {

                vm.login(u, p) { ok, farmer, err ->
                    runOnUiThread {
                        if (ok && farmer != null) {

                            val app = application as GokulaApp
                            app.currentFarmerId = farmer.id
                            app.currentFarmerName = farmer.fullName

                            // ✅ SAVE LOGIN (IMPORTANT)
                            app.saveLogin(farmer.id, farmer.fullName)

                            goToMain()

                        } else {
                            Toast.makeText(this, err ?: "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}