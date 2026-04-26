package es.ubu.bikeparkingapp.domain.usecase.auth

import es.ubu.bikeparkingapp.domain.entity.Account
import es.ubu.bikeparkingapp.domain.entity.Role

/**
 * Representa la interfaz del caso de uso para registrar un usuario.
 */
interface RegisterUseCase {
    suspend operator fun invoke(
        email: String,
        password: String,
        name: String,
        taxId: String,
        role: Role = Role.USER
    ): Result<Account>
}