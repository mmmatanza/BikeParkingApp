package es.ubu.bikeparkingapp.domain.usecase.auth

import es.ubu.bikeparkingapp.domain.repository.AuthRepository

class SignoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() =
        repository.signout()
}