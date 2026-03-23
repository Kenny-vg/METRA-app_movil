package com.softnamic.proyectointegradorii.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.softnamic.proyectointegradorii.R
import com.softnamic.proyectointegradorii.inicio.InicioActivity
import com.softnamic.proyectointegradorii.core.data.RestaurantRepository
import com.softnamic.proyectointegradorii.core.data.DataUpdater

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var btnLogin: Button
    private lateinit var pbLoading: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        btnLogin = findViewById(R.id.btn_login)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
        pbLoading = findViewById(R.id.pbLoading)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            viewModel.login(email, password)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            // Limpiar errores previos
            tilEmail.error = null
            tilPassword.error = null
            
            // Estado por defecto: no cargando
            pbLoading.visibility = View.GONE
            btnLogin.isEnabled = true
            btnLogin.text = "ACCEDER AL SISTEMA"

            when (state) {
                is LoginState.Loading -> {
                    pbLoading.visibility = View.VISIBLE
                    btnLogin.isEnabled = false
                    btnLogin.text = "" // Ocultar texto para que se vea el spinner
                }

                is LoginState.Success -> {
                    val prefs = getSharedPreferences("MY_APP", MODE_PRIVATE)
                    prefs.edit()
                        .putString("TOKEN", state.token)
                        .putString("ROLE", state.role)
                        .putString("NAME", state.name)
                        .putString("CAFE_NAME", state.cafeNombre)
                        .apply()

                    RestaurantRepository.currentToken = state.token
                    DataUpdater.startUpdating()

                    startActivity(Intent(this, InicioActivity::class.java))
                    finish()
                }

                is LoginState.EmailError -> { 
                    tilEmail.error = state.message 
                }
                is LoginState.PasswordError -> { 
                    tilPassword.error = state.message 
                }
                is LoginState.Error -> { 
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show() 
                }
                is LoginState.Idle -> { }
            }
        }
    }
}
