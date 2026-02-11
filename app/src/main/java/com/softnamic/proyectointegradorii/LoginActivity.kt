package com.softnamic.proyectointegradorii

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

import com.softnamic.proyectointegradorii.network.RetrofitClient
import com.softnamic.proyectointegradorii.data.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin = findViewById<Button>(R.id.btn_login)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)

        btnLogin.setOnClickListener {

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // --- VALIDACIONES MEJORADAS ---

            if (email.isEmpty()) {
                etEmail.error = "El email no puede estar vacío"
                return@setOnClickListener
            }

            // 1. Nueva validación para el formato de email
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Por favor, ingresa un email válido (ej: tu@correo.com)"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "La contraseña no puede estar vacía"
                return@setOnClickListener
            }

            // Limpiar errores previos antes de hacer la llamada
            etEmail.error = null
            etPassword.error = null

            // --- LLAMADA A RETROFIT ---
            val call = RetrofitClient.instance.login(email, password)

            call.enqueue(object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {

                    if (response.isSuccessful) {

                        val loginResponse = response.body()

                        if (loginResponse?.success == true) {
                            // Login exitoso
                            val token = loginResponse.data?.token

                            val prefs = getSharedPreferences("MY_APP", MODE_PRIVATE)
                            prefs.edit().putString("TOKEN", token).apply()

                            startActivity(Intent(this@LoginActivity, InicioActivity::class.java))
                            finish()

                        } else {
                            // 2. Mensaje claro de credenciales inválidas
                            val errorMessage = loginResponse?.message ?: "Credenciales inválidas. Inténtalo de nuevo."
                            Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                        }

                    } else {
                        // Error en la respuesta del servidor (ej. 404, 500)
                        Toast.makeText(this@LoginActivity, "Error del servidor (Código: ${response.code()})", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // Error de conexión o fallo en la llamada
                    Toast.makeText(this@LoginActivity, "Error de conexión. Revisa tu internet.", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}
