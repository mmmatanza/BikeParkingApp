import es.ubu.bikeparkingapp.domain.exception.InvalidCredentialsException
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import es.ubu.bikeparkingapp.domain.usecase.auth.LoginUseCaseImpl
import es.ubu.bikeparkingapp.helper.FakeAccountRepository
import es.ubu.bikeparkingapp.helper.FakeAuthRepository
import es.ubu.bikeparkingapp.helper.TestData.testAccount
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue


class LoginUseCaseTest {

    private val fakeAuthRepo = FakeAuthRepository()
    private val fakeAccountRepo = FakeAccountRepository()
    private val useCase = LoginUseCaseImpl(fakeAuthRepo, fakeAccountRepo)

    @Test
    fun `Login exitoso devuelve Unit y guarda account localmente`() = runTest {
        fakeAuthRepo.loginResult = Result.success("user-123")
        fakeAccountRepo.getAccountResult = Result.success(testAccount)

        val result = useCase("test@test.com", "pass123")

        assertTrue(result.isSuccess)
        assertEquals(testAccount, fakeAccountRepo.getCachedAccount())
    }

    @Test
    fun `Login con credenciales invalidas devuelve failure`() = runTest {
        fakeAuthRepo.loginResult = Result.failure(InvalidCredentialsException())

        val result = useCase("bad@test.com", "wrong")

        assertTrue(result.isFailure)
        assertIs<InvalidCredentialsException>(result.exceptionOrNull())
    }

    @Test
    fun `Login sin red devuelve NoNetworkException`() = runTest {
        fakeAuthRepo.loginResult = Result.failure(NoNetworkException())

        val result = useCase("test@test.com", "pass123")

        assertTrue(result.isFailure)
        assertIs<NoNetworkException>(result.exceptionOrNull())
    }

    @Test
    fun `Login exitoso pero falla getAccount no guarda account localmente`() = runTest {
        fakeAuthRepo.loginResult = Result.success("user-123")
        fakeAccountRepo.getAccountResult = Result.failure(NoNetworkException())

        val result = useCase("test@test.com", "pass123")

        assertTrue(result.isFailure)
        assertNull(fakeAccountRepo.getCachedAccount())
    }

}