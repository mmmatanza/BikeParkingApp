package es.ubu.bikeparkingapp.domain.usecase.alert

/**
 * Interfaz que define el caso de uso para publicar una alerta manual en un parking.
 */
interface PublishParkingAlertUseCase {
    suspend operator fun invoke(parkingId: String, message: String): Result<Unit>
}
