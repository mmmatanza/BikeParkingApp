package es.ubu.bikeparkingapp.data.repository

import es.ubu.bikeparkingapp.config.AppConfig
import es.ubu.bikeparkingapp.data.mapper.ErrorMapper
import es.ubu.bikeparkingapp.domain.exception.InvalidCredentialsException
import es.ubu.bikeparkingapp.domain.exception.NoActiveSessionException
import es.ubu.bikeparkingapp.domain.model.AuthState
import es.ubu.bikeparkingapp.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.parseSessionFromUrl
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

/**
 * Representa la implementación del repositorio de autenticación en Supabase.
 *
 * @property client Cliente Supabase para interactuar con la base de datos.
 * @property config Configuración de la aplicación.
 */
class SupabaseAuthRepository(
    private val client: SupabaseClient,
    private val config: AppConfig
) : AuthRepository {
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

    override suspend fun login(email: String, pass: String): Result<String> {
        return runCatching {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = pass
            }
            client.auth.currentUserOrNull()?.id ?: throw NoActiveSessionException()
        }.onFailure { cause ->
            throw if (cause is RestException) InvalidCredentialsException()
            else ErrorMapper.map(cause)
        }
    }

    override suspend fun signout(): Result<Unit> {
        return runCatching {
            client.auth.signOut()
        }.onFailure { cause ->
            throw ErrorMapper.map(cause)
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
        }.onFailure { cause ->
            throw ErrorMapper.map(cause)
        }
    }

    override suspend fun requestPasswordReset(email: String): Result<Unit> {
        return runCatching {
            client.auth.resetPasswordForEmail(
                email,
                redirectUrl = config.passwordResetUrl
            )
        }.onFailure { cause ->
            throw ErrorMapper.map(cause)
        }
    }

    override suspend fun updatePassword(newPassword: String): Result<Unit> {
        return runCatching {
            client.auth.updateUser {
                password = newPassword
            }
            Unit
        }.onFailure { cause ->
            throw ErrorMapper.map(cause)
        }
    }

    override suspend fun handleDeepLink(url: String): Result<Unit> {
        return runCatching {
            val session = client.auth.parseSessionFromUrl(url)
            client.auth.importSession(session)
            Unit
        }.onFailure { cause ->
            throw ErrorMapper.map(cause)
        }
    }

    override suspend fun getCurrentUserId(): Result<String> {
        return runCatching {
            client.auth.currentUserOrNull()?.id ?: throw NoActiveSessionException()
        }.onFailure { cause ->
            throw ErrorMapper.map(cause)
        }
    }
}
