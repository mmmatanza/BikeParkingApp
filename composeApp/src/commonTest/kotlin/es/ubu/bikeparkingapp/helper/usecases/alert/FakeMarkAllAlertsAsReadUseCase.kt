package es.ubu.bikeparkingapp.helper.usecases.alert

import es.ubu.bikeparkingapp.domain.usecase.alert.MarkAllAlertsAsReadUseCase

class FakeMarkAllAlertsAsReadUseCase : MarkAllAlertsAsReadUseCase {
    var shouldReturnError = false

    override suspend fun invoke(): Result<Unit> {
        return if (shouldReturnError) {
            Result.failure(Exception("Mark all alerts as read error"))
        } else {
            Result.success(Unit)
        }
    }
}
