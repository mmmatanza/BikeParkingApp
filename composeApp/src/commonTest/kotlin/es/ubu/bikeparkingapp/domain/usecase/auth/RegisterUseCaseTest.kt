package es.ubu.bikeparkingapp.domain.usecase.auth

import es.ubu.bikeparkingapp.domain.entity.Role
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import es.ubu.bikeparkingapp.helper.repositories.FakeAccountRepository
import es.ubu.bikeparkingapp.helper.repositories.FakeAuthRepository
import es.ubu.bikeparkingapp.helper.TestData
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RegisterUseCaseTest {

    private val fakeAuthRepo = FakeAuthRepository()
    private val fakeAccountRepo = FakeAccountRepository()
    private val useCase = RegisterUseCaseImpl(fakeAuthRepo, fakeAccountRepo)

    @Test
    fun `Registro exitoso devuelve Account y cierra sesion`() = runTest {
        fakeAuthRepo.registerResult = Result.success("user-123")
        fakeAccountRepo.createAccountResult = Result.success(TestData.testAccount)
        fakeAuthRepo.signoutResult = Result.success(Unit)

        val result = useCase("test@test.com", "pass123", "Manuel", "12345678A")

        assertTrue(result.isSuccess)
        assertEquals(TestData.testAccount, result.getOrNull())
    }

    @Test
    fun `Fallo en register no llama a createAccount ni signout`() = runTest {
        fakeAuthRepo.registerResult = Result.failure(NoNetworkException())

        val result = useCase("test@test.com", "pass123", "Manuel", "12345678A")

        assertTrue(result.isFailure)
        assertNull(fakeAccountRepo.getCachedAccount())
    }

    @Test
    fun `Fallo en createAccount devuelve failure`() = runTest {
        fakeAuthRepo.registerResult = Result.success("user-123")
        fakeAccountRepo.createAccountResult = Result.failure(NoNetworkException())

        val result = useCase("test@test.com", "pass123", "Manuel", "12345678A")

        assertTrue(result.isFailure)
        assertIs<NoNetworkException>(result.exceptionOrNull())
    }

    @Test
    fun `Fallo en signout devuelve failure aunque account fue creada`() = runTest {
        fakeAuthRepo.registerResult = Result.success("user-123")
        fakeAccountRepo.createAccountResult = Result.success(TestData.testAccount)
        fakeAuthRepo.signoutResult = Result.failure(NoNetworkException())

        val result = useCase("test@test.com", "pass123", "Manuel", "12345678A")

        assertTrue(result.isFailure)
    }

    @Test
    fun `Registro con rol ADMIN devuelve account con rol correcto`() = runTest {
        val adminAccount = TestData.testAccount.copy(role = Role.ADMIN)
        fakeAuthRepo.registerResult = Result.success("user-123")
        fakeAccountRepo.createAccountResult = Result.success(adminAccount)
        fakeAuthRepo.signoutResult = Result.success(Unit)

        val result = useCase("admin@test.com", "pass123", "Manuel", "12345678A", Role.ADMIN)

        assertEquals(Role.ADMIN, result.getOrNull()?.role)
    }
}