package com.softnamic.proyectointegradorii.login

/**
 * Clase sellada que representa los diferentes estados posibles de la pantalla de login.
 */
sealed class LoginState {

    /** Estado inicial, no se ha realizado ninguna acción. */
    object Idle : LoginState()

    /** El login se está procesando (ej: mostrando una barra de progreso). */
    object Loading : LoginState()

    /** El login fue exitoso y se recibió un token. */
    data class Success(val token: String) : LoginState()

    /** Ocurrió un error durante el login. */
    data class Error(val message: String) : LoginState()
}