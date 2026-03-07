package es.ubu.bikeparkingapp.domain.usecase.auth

import es.ubu.bikeparkingapp.domain.model.AuthState
import es.ubu.bikeparkingapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

/**
 * Representa el caso de uso para obtener el estado de autenticación.
 *
 * @property authRepository Repositorio de autenticación.
 */
class GetAuthStateUseCase(private val authRepository: AuthRepository) {
    // Se obtiene el flujo de estados de autenticación del repositorio
        operator fun invoke(): Flow<AuthState> =
        authRepository.getAuthStateFlow()
}