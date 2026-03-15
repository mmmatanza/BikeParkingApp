package es.ubu.bikeparkingapp.helper

import es.ubu.bikeparkingapp.domain.entity.Account
import es.ubu.bikeparkingapp.domain.entity.Role
import es.ubu.bikeparkingapp.domain.model.AuthState
import es.ubu.bikeparkingapp.domain.usecase.auth.GetAuthStateUseCase
import es.ubu.bikeparkingapp.domain.usecase.auth.LoginUseCase
import es.ubu.bikeparkingapp.domain.usecase.auth.RegisterUseCase
import es.ubu.bikeparkingapp.domain.usecase.auth.RequestPasswordResetUseCase
import es.ubu.bikeparkingapp.helper.TestData.testAccount
import kotlinx.coroutines.flow.flowOf

class FakeLoginUseCase : LoginUseCase {
    var result: Result<Unit> = Result.success(Unit)
    override suspend fun invoke(email: String, pass: String): Result<Unit> = result
}

class FakeGetAuthStateUseCase : GetAuthStateUseCase {
    override fun invoke() = flowOf(AuthState.Unauthenticated)
}

class FakeRequestPasswordResetUseCase : RequestPasswordResetUseCase {
    var result: Result<Unit> = Result.success(Unit)
    override suspend fun invoke(email: String): Result<Unit> = result
}

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