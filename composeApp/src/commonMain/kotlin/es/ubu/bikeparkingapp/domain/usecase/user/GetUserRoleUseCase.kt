package es.ubu.bikeparkingapp.domain.usecase.user

import es.ubu.bikeparkingapp.domain.entity.Role

/**
 * Representa la interfaz del caso de uso para obtener el rol de un usuario.
 *
 */
interface GetUserRoleUseCase {
    suspend operator fun invoke(): Result<Role>
}