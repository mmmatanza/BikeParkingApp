package es.ubu.bikeparkingapp.domain.repository

import es.ubu.bikeparkingapp.domain.entity.Alert

/**
 * Interfaz que define el repositorio de alertas.
 */
interface AlertRepository {
    suspend fun getAlertsByAccountId(accountId: String): Result<List<Alert>>
    suspend fun markAsRead(alertId: String): Result<Unit>
    suspend fun markAllAsRead(accountId: String): Result<Unit>
    suspend fun publishParkingAlert(parkingId: String, message: String): Result<Unit>
}
