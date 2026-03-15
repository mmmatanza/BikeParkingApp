package es.ubu.bikeparkingapp.integration

import es.ubu.bikeparkingapp.domain.entity.Role
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import es.ubu.bikeparkingapp.domain.usecase.auth.RegisterUseCaseImpl
import es.ubu.bikeparkingapp.helper.FakeAccountRepository
import es.ubu.bikeparkingapp.helper.FakeAuthRepository
import es.ubu.bikeparkingapp.helper.TestData.testAccount
import es.ubu.bikeparkingapp.presentation.feature.register.RegisterViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RegisterIntegrationTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var fakeAccountRepository: FakeAccountRepository
    private lateinit var viewModel: RegisterViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeAuthRepository = FakeAuthRepository()
        fakeAccountRepository = FakeAccountRepository()
        val registerUseCase = RegisterUseCaseImpl(fakeAuthRepository, fakeAccountRepository)
        viewModel = RegisterViewModel(registerUseCase)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun setValidState() {
        viewModel.onNameChange("Manuel García")
        viewModel.onEmailChange("user@example.com")
        viewModel.onTaxIdChange("12345678A")
        viewModel.onPasswordChange("Secure@123")
        viewModel.onPasswordConfirmationChange("Secure@123")
        viewModel.onRoleChange(Role.USER)
    }

    @Test
    fun `Registro exitoso establece isSuccess y devuelve account`() = runTest(testDispatcher) {
        fakeAuthRepository.registerResult = Result.success("user-123")
        fakeAccountRepository.createAccountResult = Result.success(testAccount)
        fakeAuthRepository.signoutResult = Result.success(Unit)
        setValidState()

        viewModel.onRegisterClick()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value.isSuccess)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `Registro con NoNetworkException en auth establece error en el estado`() = runTest(testDispatcher) {
        fakeAuthRepository.registerResult = Result.failure(NoNetworkException())
        setValidState()

        viewModel.onRegisterClick()
        testDispatcher.scheduler.advanceUntilIdle()

        assertIs<NoNetworkException>(viewModel.state.value.error)
        assertFalse(viewModel.state.value.isSuccess)
    }

    @Test
    fun `Registro exitoso en auth pero falla createAccount establece error`() = runTest(testDispatcher) {
        fakeAuthRepository.registerResult = Result.success("user-123")
        fakeAccountRepository.createAccountResult = Result.failure(NoNetworkException())
        setValidState()

        viewModel.onRegisterClick()
        testDispatcher.scheduler.advanceUntilIdle()

        assertIs<NoNetworkException>(viewModel.state.value.error)
        assertFalse(viewModel.state.value.isSuccess)
    }

    @Test
    fun `Registro exitoso pero falla signout establece error`() = runTest(testDispatcher) {
        fakeAuthRepository.registerResult = Result.success("user-123")
        fakeAccountRepository.createAccountResult = Result.success(testAccount)
        fakeAuthRepository.signoutResult = Result.failure(NoNetworkException())
        setValidState()

        viewModel.onRegisterClick()
        testDispatcher.scheduler.advanceUntilIdle()

        assertIs<NoNetworkException>(viewModel.state.value.error)
        assertFalse(viewModel.state.value.isSuccess)
    }
}