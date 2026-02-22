package com.softnamic.proyectointegradorii.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.softnamic.proyectointegradorii.inicio.InicioActivity
import com.softnamic.proyectointegradorii.R
import com.softnamic.proyectointegradorii.login.LoginViewModel
import com.softnamic.proyectointegradorii.login.LoginState

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        val btnLogin = findViewById<Button>(R.id.btn_login)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)

        btnLogin.setOnClickListener {

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            viewModel.login(email, password)
        }

        observeViewModel()
    }

    private fun observeViewModel() {

        viewModel.state.observe(this) { state ->

            when (state) {

                is LoginState.Loading -> {
                    // mostrar progressBar
                }

                is LoginState.Success -> {

                    val prefs = getSharedPreferences("MY_APP", MODE_PRIVATE)
                    prefs.edit().putString("TOKEN", state.token).apply()

                    startActivity(Intent(this, InicioActivity::class.java))
                    finish()
                }

                is LoginState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }

                else -> {}
            }
        }
    }
}