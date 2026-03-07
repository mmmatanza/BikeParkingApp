package es.ubu.bikeparkingapp.data.repository

import es.ubu.bikeparkingapp.domain.entity.Role
import es.ubu.bikeparkingapp.domain.model.AuthState
import es.ubu.bikeparkingapp.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

/**
 * Representa la implementación del repositorio de autenticación en Supabase.
 *
 * @property client Cliente Supabase para interactuar con la base de datos.
 */
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

    override suspend fun register(
        email: String,
        password: String
    ): Result<String> {
        return runCatching {
            val result = client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            result?.id ?: error("No user after register")
        }.recoverCatching {
            throw Exception("Ha ocurrido un error. Inténtalo de nuevo.")
        }
    }

    override suspend fun getCurrentUserId(): Result<String> {
        return runCatching {
            client.auth.currentUserOrNull()?.id ?: error("El usuario no tiene sesión activa.")
        }
    }
}