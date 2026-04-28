package es.ubu.bikeparkingapp.helper.usecases.user

import es.ubu.bikeparkingapp.domain.usecase.user.GetUserIdUseCase

class FakeGetUserIdUseCase : GetUserIdUseCase {

    var response: String = "fake_user_id"

    var shouldFail = false
    var exception: Throwable = Exception("Fake error")

    override suspend operator fun invoke(): Result<String> {

        return if (shouldFail) {
            Result.failure(exception)
        } else {
            Result.success(response)
        }
    }

}