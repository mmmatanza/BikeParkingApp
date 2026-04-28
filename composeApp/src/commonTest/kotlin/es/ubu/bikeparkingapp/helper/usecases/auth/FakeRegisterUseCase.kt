package es.ubu.bikeparkingapp.helper.usecases.auth

import es.ubu.bikeparkingapp.domain.entity.Account
import es.ubu.bikeparkingapp.domain.entity.Role
import es.ubu.bikeparkingapp.domain.usecase.auth.RegisterUseCase
import es.ubu.bikeparkingapp.helper.TestData

class FakeRegisterUseCase : RegisterUseCase {
    var result: Result<Account> = Result.success(TestData.testAccount)
    override suspend fun invoke(
        email: String,
        password: String,
        name: String,
        taxId: String,
        role: Role
    ): Result<Account> = result
}