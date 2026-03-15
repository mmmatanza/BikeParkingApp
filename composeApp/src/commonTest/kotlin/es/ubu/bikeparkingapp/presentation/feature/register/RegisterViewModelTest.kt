package es.ubu.bikeparkingapp.presentation.feature.register

import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import es.ubu.bikeparkingapp.helper.FakeRegisterUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import es.ubu.bikeparkingapp.domain.entity.Role
import es.ubu.bikeparkingapp.domain.exception.EmailInvalidException
import es.ubu.bikeparkingapp.domain.exception.RegisterException
import es.ubu.bikeparkingapp.helper.TestData.testAccount
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeRegisterUseCase: FakeRegisterUseCase
    private lateinit var viewModel: RegisterViewModel

    // Estado válido de base para reutilizar en tests del UseCase
    private fun setValidState() {
        viewModel.onNameChange("Manuel García")
        viewModel.onEmailChange("user@example.com")
        viewModel.onTaxIdChange("12345678A")
        viewModel.onPasswordChange("Secure@123")
        viewModel.onPasswordConfirmationChange("Secure@123")
        viewModel.onRoleChange(Role.USER)
    }

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRegisterUseCase = FakeRegisterUseCase()
        viewModel = RegisterViewModel(fakeRegisterUseCase)
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
    fun `onPasswordChange actualiza la password en el estado`() {
        viewModel.onPasswordChange("pass123")
        assertEquals("pass123", viewModel.state.value.password)
    }

    @Test
    fun `onPasswordConfirmationChange actualiza la confirmacion en el estado`() {
        viewModel.onPasswordConfirmationChange("pass123")
        assertEquals("pass123", viewModel.state.value.passwordConfirmation)
    }

    @Test
    fun `onNameChange actualiza el nombre en el estado`() {
        viewModel.onNameChange("Manuel")
        assertEquals("Manuel", viewModel.state.value.name)
    }

    @Test
    fun `onTaxIdChange actualiza el taxId en el estado`() {
        viewModel.onTaxIdChange("12345678A")
        assertEquals("12345678A", viewModel.state.value.taxId)
    }

    @Test
    fun `onRoleChange actualiza el role en el estado`() {
        viewModel.onRoleChange(Role.ADMIN)
        assertEquals(Role.ADMIN, viewModel.state.value.role)
    }


    @Test
    fun `clearError limpia el error del estado`() {
        viewModel.onRegisterClick() // dispara NameEmptyException
        viewModel.clearError()
        assertNull(viewModel.state.value.error)
    }


    @Test
    fun `onRegisterClick con nombre vacio establece NameEmptyException`() {
        viewModel.onNameChange("")
        viewModel.onEmailChange("user@example.com")
        viewModel.onTaxIdChange("12345678A")
        viewModel.onPasswordChange("Secure@123")
        viewModel.onPasswordConfirmationChange("Secure@123")

        viewModel.onRegisterClick()

        assertIs<RegisterException.NameEmptyException>(viewModel.state.value.error)
    }

    @Test
    fun `onRegisterClick con email invalido establece EmailInvalidException`() {
        viewModel.onNameChange("Manuel")
        viewModel.onEmailChange("no-es-email")
        viewModel.onTaxIdChange("12345678A")
        viewModel.onPasswordChange("Secure@123")
        viewModel.onPasswordConfirmationChange("Secure@123")

        viewModel.onRegisterClick()

        assertIs<EmailInvalidException>(viewModel.state.value.error)
    }

    @Test
    fun `onRegisterClick con taxId vacio establece TaxIdEmptyException`() {
        viewModel.onNameChange("Manuel")
        viewModel.onEmailChange("user@example.com")
        viewModel.onTaxIdChange("")
        viewModel.onPasswordChange("Secure@123")
        viewModel.onPasswordConfirmationChange("Secure@123")

        viewModel.onRegisterClick()

        assertIs<RegisterException.TaxIdEmptyException>(viewModel.state.value.error)
    }

    @Test
    fun `onRegisterClick con passwords que no coinciden establece PasswordMismatchException`() {
        viewModel.onNameChange("Manuel")
        viewModel.onEmailChange("user@example.com")
        viewModel.onTaxIdChange("12345678A")
        viewModel.onPasswordChange("Secure@123")
        viewModel.onPasswordConfirmationChange("Diferente@123")

        viewModel.onRegisterClick()

        assertIs<RegisterException.PasswordMismatchException>(viewModel.state.value.error)
    }

    @Test
    fun `onRegisterClick con password corta establece WeakPasswordException`() {
        viewModel.onNameChange("Manuel")
        viewModel.onEmailChange("user@example.com")
        viewModel.onTaxIdChange("12345678A")
        viewModel.onPasswordChange("Ab@1")
        viewModel.onPasswordConfirmationChange("Ab@1")

        viewModel.onRegisterClick()

        assertIs<RegisterException.WeakPasswordException>(viewModel.state.value.error)
    }

    @Test
    fun `onRegisterClick con password sin mayuscula establece WeakPasswordException`() {
        viewModel.onNameChange("Manuel")
        viewModel.onEmailChange("user@example.com")
        viewModel.onTaxIdChange("12345678A")
        viewModel.onPasswordChange("secure@123")
        viewModel.onPasswordConfirmationChange("secure@123")

        viewModel.onRegisterClick()

        assertIs<RegisterException.WeakPasswordException>(viewModel.state.value.error)
    }

    @Test
    fun `onRegisterClick con password sin caracter especial establece WeakPasswordException`() {
        viewModel.onNameChange("Manuel")
        viewModel.onEmailChange("user@example.com")
        viewModel.onTaxIdChange("12345678A")
        viewModel.onPasswordChange("Secure123")
        viewModel.onPasswordConfirmationChange("Secure123")

        viewModel.onRegisterClick()

        assertIs<RegisterException.WeakPasswordException>(viewModel.state.value.error)
    }

    @Test
    fun `onRegisterClick exitoso establece isSuccess a true`() = runTest(testDispatcher) {
        fakeRegisterUseCase.result = Result.success(testAccount)
        setValidState()

        viewModel.onRegisterClick()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value.isSuccess)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `onRegisterClick con NoNetworkException establece el error`() = runTest(testDispatcher) {
        fakeRegisterUseCase.result = Result.failure(NoNetworkException())
        setValidState()

        viewModel.onRegisterClick()
        testDispatcher.scheduler.advanceUntilIdle()

        assertIs<NoNetworkException>(viewModel.state.value.error)
        assertFalse(viewModel.state.value.isSuccess)
    }

    @Test
    fun `onRegisterClick con excepcion desconocida establece Exception generica`() = runTest(testDispatcher) {
        fakeRegisterUseCase.result = Result.failure(RuntimeException("Fallo inesperado"))
        setValidState()

        viewModel.onRegisterClick()
        testDispatcher.scheduler.advanceUntilIdle()

        val error = viewModel.state.value.error
        assertIs<Exception>(error)
        assertEquals("Fallo inesperado", error.message)
        assertFalse(viewModel.state.value.isSuccess)
    }

}