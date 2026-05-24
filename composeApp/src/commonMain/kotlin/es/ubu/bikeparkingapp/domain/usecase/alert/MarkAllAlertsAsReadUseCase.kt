package es.ubu.bikeparkingapp.domain.usecase.alert

/**
 * Interfaz para el caso de uso de marcar todas las alertas como leídas.
 */
interface MarkAllAlertsAsReadUseCase {
    suspend operator fun invoke(): Result<Unit>
}
