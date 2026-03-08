package es.ubu.bikeparkingapp.domain.repository

import es.ubu.bikeparkingapp.domain.model.AuthState
import kotlinx.coroutines.flow.Flow

/**
 * Representa el repositorio de autenticación.
 *
 */
interface AuthRepository {

    fun getAuthStateFlow(): Flow<AuthState>
    suspend fun getCurrentUserId(): Result<String>
    suspend fun login(email: String, pass: String): Result<String>
    suspend fun signout(): Result<Unit>
    suspend fun register(email: String, password: String): Result<String>
    suspend fun requestPasswordReset(email: String): Result<Unit>


}