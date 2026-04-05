package es.ubu.bikeparkingapp.domain.usecase.auth

/**
 * Representa la interfaz del caso de uso para cerrar sesión.
 *
 */
interface SignoutUseCase {
    suspend operator fun invoke(): Result<Unit>
}