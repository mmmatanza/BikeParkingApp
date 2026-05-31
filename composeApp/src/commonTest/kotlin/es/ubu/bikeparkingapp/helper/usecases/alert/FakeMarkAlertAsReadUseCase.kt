package es.ubu.bikeparkingapp.helper.usecases.alert

import es.ubu.bikeparkingapp.domain.usecase.alert.MarkAlertAsReadUseCase

class FakeMarkAlertAsReadUseCase : MarkAlertAsReadUseCase {
    var shouldReturnError = false

    override suspend fun invoke(alertId: String): Result<Unit> {
        return if (shouldReturnError) {
            Result.failure(Exception("Mark alert as read error"))
        } else {
            Result.success(Unit)
        }
    }
}
