package es.ubu.bikeparkingapp.helper.repositories

import es.ubu.bikeparkingapp.domain.entity.Alert
import es.ubu.bikeparkingapp.domain.repository.AlertRepository

class FakeAlertRepository : AlertRepository {
    val alerts = mutableListOf<Alert>()
    var shouldReturnError = false

    override suspend fun getAlertsByAccountId(accountId: String): Result<List<Alert>> {
        return if (shouldReturnError) {
            Result.failure(Exception("Alert error"))
        } else {
            Result.success(alerts.filter { it.accountId == accountId })
        }
    }

    override suspend fun markAsRead(alertId: String): Result<Unit> {
        return if (shouldReturnError) {
            Result.failure(Exception("Alert error"))
        } else {
            val index = alerts.indexOfFirst { it.alertId == alertId }
            if (index != -1) {
                alerts[index] = alerts[index].copy(isRead = true)
            }
            Result.success(Unit)
        }
    }

    override suspend fun markAllAsRead(accountId: String): Result<Unit> {
        return if (shouldReturnError) {
            Result.failure(Exception("Alert error"))
        } else {
            alerts.filter { it.accountId == accountId }.forEachIndexed { index, alert ->
                val listIndex = alerts.indexOf(alert)
                alerts[listIndex] = alert.copy(isRead = true)
            }
            Result.success(Unit)
        }
    }

    override suspend fun publishParkingAlert(
        parkingId: String,
        message: String
    ): Result<Unit> {
        return if (shouldReturnError) {
            Result.failure(Exception("Alert error"))
        } else {
            Result.success(Unit)
        }
    }
}
