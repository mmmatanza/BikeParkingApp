package es.ubu.bikeparkingapp.domain.usecase.auth

import es.ubu.bikeparkingapp.domain.model.AuthState
import kotlinx.coroutines.flow.Flow

/**
 * Representa la interfaz del caso de uso para obtener el estado de autenticación.
 */
interface GetAuthStateUseCase {
    // Se obtiene el flujo de estados de autenticación del repositorio
        operator fun invoke(): Flow<AuthState>
}