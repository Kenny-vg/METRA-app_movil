package com.softnamic.proyectointegradorii.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.softnamic.proyectointegradorii.R
import com.softnamic.proyectointegradorii.inicio.InicioActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        val btnLogin = findViewById<Button>(R.id.btn_login)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            viewModel.login(email, password)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->

            // Reset errors
            etEmail.error = null
            etPassword.error = null

            when (state) {
                is LoginState.Loading -> {
                    // mostrar progressBar
                }

                is LoginState.Success -> {
                    val prefs = getSharedPreferences("MY_APP", MODE_PRIVATE)
                    prefs.edit()
                        .putString("TOKEN", state.token)
                        .putString("ROLE", state.role)
                        .apply()
                    startActivity(Intent(this, InicioActivity::class.java))
                    finish()
                }

                is LoginState.EmailError -> {
                    etEmail.error = state.message
                }

                is LoginState.PasswordError -> {
                    etPassword.error = state.message
                }

                is LoginState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }

                is LoginState.Idle -> {
                    // Nothing to do
                }
            }
        }
    }
}