package es.ubu.bikeparkingapp.data.repositories

import es.ubu.bikeparkingapp.domain.model.AuthState
import es.ubu.bikeparkingapp.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

class SupabaseAuthRepository(private val client: SupabaseClient) : AuthRepository {
    override fun getAuthStateFlow(): Flow<AuthState> =
        client.auth.sessionStatus.transform { status ->
            emit(
                // La conversión a AuthState es importante para desacoplar la lógica y facilitar futuros
                // cambios de implementación
                when (status) {
                    is SessionStatus.Authenticated -> AuthState.Authenticated
                    is SessionStatus.NotAuthenticated -> AuthState.Unauthenticated
                    is SessionStatus.Initializing -> AuthState.Loading
                    is SessionStatus.RefreshFailure -> AuthState.Unauthenticated
                }
            )
        }

    override suspend fun login(email: String, pass: String): Result<Unit> {
        return runCatching {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = pass
            }
        }
    }

    override suspend fun signout(): Result<Unit> {
        return runCatching {
            client.auth.signOut()
        }
    }
}