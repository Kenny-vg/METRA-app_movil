package com.softnamic.proyectointegradorii.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repository = LoginRepository()

    private val _state = MutableLiveData<LoginState>(LoginState.Idle)
    val state: LiveData<LoginState> = _state

    fun login(email: String, password: String) {

        _state.value = LoginState.Idle

        if (email.isBlank()) {
            _state.value = LoginState.EmailError("El correo es obligatorio")
            return
        }

        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})".toRegex()
        if (!email.matches(emailRegex)) {
            _state.value = LoginState.EmailError("Formato de correo inválido. Ejemplo: correo@dominio.com")
            return
        }

        if (password.isBlank()) {
            _state.value = LoginState.PasswordError("La contraseña es obligatoria")
            return
        }



        _state.value = LoginState.Loading

        viewModelScope.launch {

            try {

                val response = repository.login(email, password)

                when (response.code()) {

                    200 -> {

                        val body = response.body()

                        if (body?.success == true) {

                            val token = body.data?.token ?: ""
                            val role = body.data?.usuario?.role?.lowercase()?.trim() ?: ""
                            val name = body.data?.usuario?.name ?: ""
                            val allowedRoles = listOf("gerente", "personal")

                            if (allowedRoles.contains(role)) {

                                _state.value = LoginState.Success(token, role, name)

                            } else {

                                _state.value =
                                    LoginState.Error("No tienes permisos para ingresar")
                            }

                        } else {

                            _state.value =
                                LoginState.Error(body?.message ?: "Error")
                        }
                    }

                    401 -> {
                        _state.value =
                            LoginState.Error("Credenciales inválidas")
                    }

                    else -> {
                        val errorBody = response.errorBody()?.string()

                        val message = try {
                            val json = org.json.JSONObject(errorBody ?: "")
                            json.optString("message", "Error ${response.code()}")
                        } catch (e: Exception) {
                            "Error ${response.code()}"
                        }

                        println("ERROR BACKEND: $message")

                        _state.value = LoginState.Error(message)
                    }
                }

            } catch (e: Exception) {
                _state.value =
                    LoginState.Error("Error de conexión")
            }
        }
    }
}
