package es.ubu.bikeparkingapp.domain.usecase.auth

/**
 * Representa la interfaz del caso de uso para iniciar sesión.
 */
interface LoginUseCase{
    suspend operator fun invoke(email: String, pass: String):Result<Unit>

}