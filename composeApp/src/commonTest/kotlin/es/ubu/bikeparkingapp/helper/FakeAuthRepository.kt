package es.ubu.bikeparkingapp.helper

import es.ubu.bikeparkingapp.domain.model.AuthState
import es.ubu.bikeparkingapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeAuthRepository : AuthRepository {

    var authStateFlowValue: Flow<AuthState> = flowOf(AuthState.Unauthenticated)
    var currentUserIdResult: Result<String> = Result.failure(NotImplementedError())
    var loginResult: Result<String> = Result.failure(NotImplementedError())
    var signoutResult: Result<Unit> = Result.success(Unit)
    var registerResult: Result<String> = Result.failure(NotImplementedError())
    var requestPasswordResetResult: Result<Unit> = Result.success(Unit)

    override fun getAuthStateFlow(): Flow<AuthState> = authStateFlowValue

    override suspend fun getCurrentUserId(): Result<String> = currentUserIdResult
    override suspend fun login(email: String, pass: String): Result<String> = loginResult
    override suspend fun signout(): Result<Unit> = signoutResult
    override suspend fun register(email: String, password: String): Result<String> = registerResult
    override suspend fun requestPasswordReset(email: String): Result<Unit> = requestPasswordResetResult
}