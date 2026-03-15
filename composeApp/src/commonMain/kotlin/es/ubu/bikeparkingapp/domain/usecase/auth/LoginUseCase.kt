package es.ubu.bikeparkingapp.domain.usecase.auth

import es.ubu.bikeparkingapp.domain.repository.AccountRepository
import es.ubu.bikeparkingapp.domain.repository.AuthRepository

/**
 * Representa la interfaz del caso de uso para iniciar sesión.
 *
 */
interface LoginUseCase{
    suspend operator fun invoke(email: String, pass: String):Result<Unit>

}