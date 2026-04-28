package es.ubu.bikeparkingapp.helper.usecases.auth

import es.ubu.bikeparkingapp.domain.usecase.auth.LoginUseCase

class FakeLoginUseCase : LoginUseCase {
    var result: Result<Unit> = Result.success(Unit)
    override suspend fun invoke(email: String, pass: String): Result<Unit> = result
}