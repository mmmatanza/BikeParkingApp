package es.ubu.bikeparkingapp.domain.usecase.alert

import es.ubu.bikeparkingapp.domain.entity.Alert

/**
 * Interfaz para el caso de uso de obtener alertas.
 */
interface GetAlertsUseCase {
    suspend operator fun invoke(): Result<List<Alert>>
}
