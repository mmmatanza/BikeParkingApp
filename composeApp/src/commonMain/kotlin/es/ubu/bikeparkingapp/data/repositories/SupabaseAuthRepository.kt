package es.ubu.bikeparkingapp.data.repositories

import es.ubu.bikeparkingapp.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class SupabaseAuthRepository(private val client: SupabaseClient) : AuthRepository {
    override suspend fun login(email: String, pass: String): Result<Unit> {
        return runCatching {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = pass
            }
        }
    }
}