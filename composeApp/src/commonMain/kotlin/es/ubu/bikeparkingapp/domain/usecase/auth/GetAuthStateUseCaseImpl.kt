package es.ubu.bikeparkingapp.domain.usecase.auth

import es.ubu.bikeparkingapp.domain.model.AuthState
import es.ubu.bikeparkingapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

/**
 * Implementación del caso de uso para obtener el estado de autenticación.
 * @property authRepository Repositorio de autenticación.
 * @return Flujo de estados de autenticación.
 */
class GetAuthStateUseCaseImpl(
    private val authRepository: AuthRepository
): GetAuthStateUseCase {
    // Se obtiene el flujo de estados de autenticación del repositorio
    override operator fun invoke(): Flow<AuthState> =
        authRepository.getAuthStateFlow()
}