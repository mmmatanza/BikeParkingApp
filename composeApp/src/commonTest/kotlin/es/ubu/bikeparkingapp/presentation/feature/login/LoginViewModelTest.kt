package es.ubu.bikeparkingapp.presentation.feature.login

import es.ubu.bikeparkingapp.domain.exception.EmailInvalidException
import es.ubu.bikeparkingapp.domain.exception.InvalidCredentialsException
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import es.ubu.bikeparkingapp.domain.exception.PasswordEmptyException
import es.ubu.bikeparkingapp.helper.FakeGetAuthStateUseCase
import es.ubu.bikeparkingapp.helper.FakeLoginUseCase
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

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeLoginUseCase: FakeLoginUseCase
    private lateinit var fakeGetAuthStateUseCase: FakeGetAuthStateUseCase
    private lateinit var viewModel: LoginViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeLoginUseCase = FakeLoginUseCase()
        fakeGetAuthStateUseCase = FakeGetAuthStateUseCase()
        viewModel = LoginViewModel(fakeLoginUseCase, fakeGetAuthStateUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onEmailChange actualiza el email en el estado`() {
        viewModel.onEmailChange("test@example.com")
        assertEquals("test@example.com", viewModel.state.value.email)
    }

    @Test
    fun `onPasswordChange actualiza la contraseña en el estado`() {
        viewModel.onPasswordChange("secret123")
        assertEquals("secret123", viewModel.state.value.password)
    }

    @Test
    fun `clearError limpia el error del estado`() {
        viewModel.onEmailChange("bad")
        viewModel.onLoginClick()
        viewModel.clearError()
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `onLoginClick con email invalido establece EmailInvalidException`() {
        viewModel.onEmailChange("no-es-un-email")
        viewModel.onPasswordChange("password123")

        viewModel.onLoginClick()

        assertIs<EmailInvalidException>(viewModel.state.value.error)
    }

    @Test
    fun `onLoginClick con email vacio establece EmailInvalidException`() {
        viewModel.onEmailChange("")
        viewModel.onPasswordChange("password123")

        viewModel.onLoginClick()

        assertIs<EmailInvalidException>(viewModel.state.value.error)
    }

    @Test
    fun `onLoginClick con password vacio establece PasswordEmptyException`() {
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("")

        viewModel.onLoginClick()

        assertIs<PasswordEmptyException>(viewModel.state.value.error)
    }

    @Test
    fun `onLoginClick con password en blanco establece PasswordEmptyException`() {
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("   ")

        viewModel.onLoginClick()

        assertIs<PasswordEmptyException>(viewModel.state.value.error)
    }

    @Test
    fun `onLoginClick exitoso no establece error`() = runTest(testDispatcher) {
        fakeLoginUseCase.result = Result.success(Unit)
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("password123")

        viewModel.onLoginClick()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `onLoginClick con NoNetworkException establece el error`() = runTest(testDispatcher) {
        fakeLoginUseCase.result = Result.failure(NoNetworkException())
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("password123")

        viewModel.onLoginClick()
        testDispatcher.scheduler.advanceUntilIdle()

        assertIs<NoNetworkException>(viewModel.state.value.error)
    }

    @Test
    fun `onLoginClick con InvalidCredentialsException establece el error`() = runTest(testDispatcher) {
        fakeLoginUseCase.result = Result.failure(InvalidCredentialsException())
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("password123")

        viewModel.onLoginClick()
        testDispatcher.scheduler.advanceUntilIdle()

        assertIs<InvalidCredentialsException>(viewModel.state.value.error)
    }

    @Test
    fun `onLoginClick con excepcion desconocida setea Exception generica`() = runTest(testDispatcher) {
        fakeLoginUseCase.result = Result.failure(RuntimeException("Error inesperado"))
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("password123")

        viewModel.onLoginClick()
        testDispatcher.scheduler.advanceUntilIdle()

        val error = viewModel.state.value.error
        assertIs<Exception>(error)
        assertEquals("Error inesperado", error.message)
    }

}