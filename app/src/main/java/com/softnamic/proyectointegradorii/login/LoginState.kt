package com.softnamic.proyectointegradorii.login

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: String) : LoginState()
    data class EmailError(val message: String) : LoginState()
    data class PasswordError(val message: String) : LoginState()
    data class Error(val message: String) : LoginState()
}