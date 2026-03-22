package com.softnamic.proyectointegradorii.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.json.JSONObject

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
                        val cafeNombre = user?.nombre_cafeteria ?: "CAFETERÍA"

                        val allowedRoles = listOf("gerente", "personal")
                        if (allowedRoles.contains(role)) {
                            _state.value = LoginState.Success(token, role, name, cafeNombre)
                        } else {
                            _state.value = LoginState.Error("No tienes permisos para acceder")
                        }
                    } else {
                        _state.value = LoginState.Error(body?.message ?: "Usuario o contraseña no válidos")
                    }
                } else {
                    // Manejo de errores por código de estado
                    val errorMessage = when (response.code()) {
                        401 -> "El usuario no existe o la contraseña es incorrecta"
                        404 -> "Servicio no disponible"
                        500 -> "Error en el servidor. Inténtalo más tarde"
                        else -> {
                            // Intentar extraer el mensaje de error del body de la respuesta
                            try {
                                val errorBody = response.errorBody()?.string()
                                val json = JSONObject(errorBody ?: "")
                                json.optString("message", "Error: ${response.code()}")
                            } catch (e: Exception) {
                                "Error: ${response.code()}"
                            }
                        }
                    }
                    _state.value = LoginState.Error(errorMessage)
                }
            } catch (e: Exception) {
                // El CATCH para errores de red o excepciones fatales
                e.printStackTrace()
                _state.value = LoginState.Error("No se pudo conectar con el servidor. Revisa tu internet.")
            }
        }
    }
}
