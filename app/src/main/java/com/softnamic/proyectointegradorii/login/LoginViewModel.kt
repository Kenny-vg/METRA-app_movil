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
            _state.value = LoginState.EmailError("Formato de correo inválido")
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
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        val user = body.data?.usuario
                        val token = body.data?.token ?: ""
                        val role = user?.role?.lowercase()?.trim() ?: ""
                        val name = user?.name ?: ""
                        // Usamos el nombre real de la propiedad de la API: nombre_cafeteria
                        val cafeNombre = user?.nombre_cafeteria ?: "CAFETERÍA"

                        val allowedRoles = listOf("gerente", "personal")
                        if (allowedRoles.contains(role)) {
                            _state.value = LoginState.Success(token, role, name, cafeNombre)
                        } else {
                            _state.value = LoginState.Error("No tienes permisos")
                        }
                    } else {
                        _state.value = LoginState.Error(body?.message ?: "Error")
                    }
                } else {
                    _state.value = LoginState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _state.value = LoginState.Error("Error de conexión")
            }
        }
    }
}
