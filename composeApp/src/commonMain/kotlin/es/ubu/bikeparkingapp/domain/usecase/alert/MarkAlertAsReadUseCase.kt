package es.ubu.bikeparkingapp.domain.usecase.alert

/**
 * Interfaz para el caso de uso de marcar una alerta como leída.
 */
interface MarkAlertAsReadUseCase {
    suspend operator fun invoke(alertId: String): Result<Unit>
}
