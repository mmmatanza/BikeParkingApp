package es.ubu.bikeparkingapp.domain.usecase.auth

/**
 * Representa la interfaz del caso de uso para restablecer la contraseña.
 */
interface RequestPasswordResetUseCase{
    suspend operator fun invoke(email:String): Result<Unit>
}