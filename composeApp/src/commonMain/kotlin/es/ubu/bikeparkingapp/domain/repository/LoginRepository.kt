package es.ubu.bikeparkingapp.domain.repository

interface AuthRepository {
    suspend fun login(email: String, pass: String): Result<Unit>
}