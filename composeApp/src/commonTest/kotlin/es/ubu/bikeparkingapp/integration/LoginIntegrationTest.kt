package es.ubu.bikeparkingapp.integration

import es.ubu.bikeparkingapp.domain.exception.InvalidCredentialsException
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import es.ubu.bikeparkingapp.domain.usecase.auth.GetAuthStateUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.auth.LoginUseCaseImpl
import es.ubu.bikeparkingapp.helper.FakeAccountRepository
import es.ubu.bikeparkingapp.helper.FakeAuthRepository
import es.ubu.bikeparkingapp.helper.TestData.testAccount
import es.ubu.bikeparkingapp.presentation.feature.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class LoginIntegrationTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var fakeAccountRepository: FakeAccountRepository
    private lateinit var viewModel: LoginViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeAuthRepository = FakeAuthRepository()
        fakeAccountRepository = FakeAccountRepository()
        val loginUseCase = LoginUseCaseImpl(fakeAuthRepository, fakeAccountRepository)
        val getAuthStateUseCase = GetAuthStateUseCaseImpl(fakeAuthRepository)
        viewModel = LoginViewModel(loginUseCase, getAuthStateUseCase)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Login exitoso guarda account y no establece error`() = runTest(testDispatcher) {
        fakeAuthRepository.loginResult = Result.success("user-123")
        fakeAccountRepository.getAccountResult = Result.success(testAccount)

        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("Secure@123")
        viewModel.onLoginClick()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.state.value.error)
        assertEquals(testAccount, fakeAccountRepository.getCachedAccount())
    }

    @Test
    fun `Login con credenciales invalidas establece InvalidCredentialsException en el estado`() = runTest(testDispatcher) {
        fakeAuthRepository.loginResult = Result.failure(InvalidCredentialsException())

        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("Secure@123")
        viewModel.onLoginClick()
        testDispatcher.scheduler.advanceUntilIdle()

        assertIs<InvalidCredentialsException>(viewModel.state.value.error)
    }

    @Test
    fun `Login exitoso pero falla getAccount establece error en el estado`() = runTest(testDispatcher) {
        fakeAuthRepository.loginResult = Result.success("user-123")
        fakeAccountRepository.getAccountResult = Result.failure(NoNetworkException())

        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("Secure@123")
        viewModel.onLoginClick()
        testDispatcher.scheduler.advanceUntilIdle()

        assertIs<NoNetworkException>(viewModel.state.value.error)
        assertNull(fakeAccountRepository.getCachedAccount())
    }
}