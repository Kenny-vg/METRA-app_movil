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

        if (!email.endsWith("@sidugow.com", ignoreCase = true)) {
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
                            _state.value =
                                LoginState.Success(body.data?.token ?: "")
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
                        _state.value =
                            LoginState.Error("Error ${response.code()}")
                    }
                }

            } catch (e: Exception) {
                _state.value =
                    LoginState.Error("Error de conexión")
            }
        }
    }
}