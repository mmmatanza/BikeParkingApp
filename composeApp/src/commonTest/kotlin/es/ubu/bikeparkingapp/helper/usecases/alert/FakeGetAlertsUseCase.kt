package es.ubu.bikeparkingapp.helper.usecases.alert

import es.ubu.bikeparkingapp.domain.entity.Alert
import es.ubu.bikeparkingapp.domain.usecase.alert.GetAlertsUseCase

class FakeGetAlertsUseCase : GetAlertsUseCase {
    var response: List<Alert> = emptyList()
    var shouldReturnError = false

    override suspend fun invoke(): Result<List<Alert>> {
        return if (shouldReturnError) {
            Result.failure(Exception("Get alerts error"))
        } else {
            Result.success(response)
        }
    }
}
