package es.ubu.bikeparkingapp.domain.usecase.auth

import es.ubu.bikeparkingapp.domain.model.AuthState
import es.ubu.bikeparkingapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class GetAuthStateUseCase(private val repository: AuthRepository) {
    // Se obtiene el flujo de estados de autenticación del repositorio
        operator fun invoke(): Flow<AuthState> =
            repository.getAuthStateFlow()
}