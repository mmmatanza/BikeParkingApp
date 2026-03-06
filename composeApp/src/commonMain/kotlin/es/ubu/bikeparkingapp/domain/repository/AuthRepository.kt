package es.ubu.bikeparkingapp.domain.repository

import es.ubu.bikeparkingapp.domain.model.AuthState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun getAuthStateFlow(): Flow<AuthState>
    suspend fun login(email: String, pass: String): Result<Unit>
    suspend fun signout(): Result<Unit>
}