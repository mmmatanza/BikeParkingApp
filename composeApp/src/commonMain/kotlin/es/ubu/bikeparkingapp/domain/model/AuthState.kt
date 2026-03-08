package es.ubu.bikeparkingapp.domain.model

/**
 * Representa el estado de autenticación.
 *
 */
sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}