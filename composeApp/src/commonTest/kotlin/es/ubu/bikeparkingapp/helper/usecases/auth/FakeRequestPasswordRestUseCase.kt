package es.ubu.bikeparkingapp.helper.usecases.auth

import es.ubu.bikeparkingapp.domain.usecase.auth.RequestPasswordResetUseCase

class FakeRequestPasswordResetUseCase : RequestPasswordResetUseCase {
    var response: Result<Unit> = Result.success(Unit)
    override suspend fun invoke(email: String): Result<Unit> = response
}