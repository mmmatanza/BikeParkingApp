package es.ubu.bikeparkingapp.domain.model

// Indica el estado de autenticación del usuario
sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}