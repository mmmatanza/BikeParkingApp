package es.ubu.bikeparkingapp.domain.usecase

import es.ubu.bikeparkingapp.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, pass: String) =
        repository.login(email, pass)
}