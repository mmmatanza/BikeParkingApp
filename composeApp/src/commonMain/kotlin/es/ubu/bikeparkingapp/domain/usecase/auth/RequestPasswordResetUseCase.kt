package es.ubu.bikeparkingapp.domain.usecase.auth

import es.ubu.bikeparkingapp.domain.repository.AuthRepository

/**
 * Representa la interfaz del caso de uso para restablecer la contraseña.
 *
 */
interface RequestPasswordResetUseCase{
    suspend operator fun invoke(email:String): Result<Unit>
}