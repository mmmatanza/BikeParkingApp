package es.ubu.bikeparkingapp.domain.usecase.user

/**
 * Representa la interfaz del caso de uso para obtener el id de un usuario.
 *
 */
interface GetUserIdUseCase {
    suspend operator fun invoke(): Result<String>
}