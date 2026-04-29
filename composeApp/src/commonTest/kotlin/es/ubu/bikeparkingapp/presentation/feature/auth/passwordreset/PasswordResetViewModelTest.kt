package es.ubu.bikeparkingapp.presentation.feature.auth.passwordreset

import es.ubu.bikeparkingapp.domain.exception.EmailInvalidException
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import es.ubu.bikeparkingapp.helper.usecases.auth.FakeRequestPasswordResetUseCase
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
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PasswordResetViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeRequestPasswordResetUseCase: FakeRequestPasswordResetUseCase
    private lateinit var viewModel: PasswordResetViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRequestPasswordResetUseCase = FakeRequestPasswordResetUseCase()
        viewModel = PasswordResetViewModel(fakeRequestPasswordResetUseCase)
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
    fun `clearError limpia el error del estado`() {
        viewModel.onEmailChange("bad")
        viewModel.onPasswordResetClick()
        viewModel.clearError()
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `clearSuccess limpia el flag success del estado`() = runTest(testDispatcher) {
        fakeRequestPasswordResetUseCase.response = Result.success(Unit)
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordResetClick()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value.success)
        viewModel.clearSuccess()
        assertFalse(viewModel.state.value.success)
    }

    @Test
    fun `onPasswordResetClick con email invalido establece EmailInvalidException`() {
        viewModel.onEmailChange("no-es-un-email")
        viewModel.onPasswordResetClick()
        assertIs<EmailInvalidException>(viewModel.state.value.error)
    }

    @Test
    fun `onPasswordResetClick con email vacio establece EmailInvalidException`() {
        viewModel.onEmailChange("")
        viewModel.onPasswordResetClick()
        assertIs<EmailInvalidException>(viewModel.state.value.error)
    }

    @Test
    fun `onPasswordResetClick exitoso establece success a true`() = runTest(testDispatcher) {
        fakeRequestPasswordResetUseCase.response = Result.success(Unit)
        viewModel.onEmailChange("user@example.com")

        viewModel.onPasswordResetClick()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value.success)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `onPasswordResetClick con NoNetworkException establece el error`() = runTest(testDispatcher) {
        fakeRequestPasswordResetUseCase.response = Result.failure(NoNetworkException())
        viewModel.onEmailChange("user@example.com")

        viewModel.onPasswordResetClick()
        testDispatcher.scheduler.advanceUntilIdle()

        assertIs<NoNetworkException>(viewModel.state.value.error)
        assertFalse(viewModel.state.value.success)
    }

    @Test
    fun `onPasswordResetClick con excepcion desconocida establece Exception generica`() = runTest(testDispatcher) {
        fakeRequestPasswordResetUseCase.response = Result.failure(RuntimeException("Error inesperado"))
        viewModel.onEmailChange("user@example.com")

        viewModel.onPasswordResetClick()
        testDispatcher.scheduler.advanceUntilIdle()

        val error = viewModel.state.value.error
        assertIs<Exception>(error)
        assertEquals("Error inesperado", error.message)
        assertFalse(viewModel.state.value.success)
    }

}