package com.softnamic.proyectointegradorii.login

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(
        val token: String, 
        val role: String, 
        val name: String,
        val cafeNombre: String // Nuevo campo
    ) : LoginState()
    data class EmailError(val message: String) : LoginState()
    data class PasswordError(val message: String) : LoginState()
    data class Error(val message: String) : LoginState()
}
