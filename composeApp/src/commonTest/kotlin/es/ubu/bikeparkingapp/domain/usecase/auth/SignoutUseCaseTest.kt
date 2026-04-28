package es.ubu.bikeparkingapp.domain.usecase.auth

import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.repositories.FakeAccountRepository
import es.ubu.bikeparkingapp.helper.repositories.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SignoutUseCaseTest {

    private lateinit var authRepository: FakeAuthRepository
    private lateinit var accountRepository: FakeAccountRepository
    private lateinit var useCase: SignoutUseCaseImpl

    @BeforeTest
    fun setUp() {
        authRepository = FakeAuthRepository()
        accountRepository = FakeAccountRepository()
        useCase = SignoutUseCaseImpl(authRepository, accountRepository)
    }

    @Test
    fun `El cierre de sesion limpia la cuenta local y desconecta al usuario`() = runTest {
        // Preparación
        // Simulamos que hay una cuenta guardada antes de cerrar sesión
        accountRepository.saveLocally(TestData.testAccount)
        authRepository.signoutResult = Result.success(Unit)

        // Ejecución
        val result = useCase()

        // Assert
        assertTrue(result.isSuccess)
        // Verificamos que la cuenta local se ha borrado
        assertNull(accountRepository.getCachedAccount())
    }

    @Test
    fun `Si el servicio de auth falla, el caso de uso captura el error`() = runTest {
        // Preparación
        val errorEsperado = Exception("Error al conectar con el servidor de Auth")
        authRepository.signoutResult = Result.failure(errorEsperado)

        // Ejecución
        val result = useCase()

        // Assert
        assertTrue(result.isSuccess)
    }
}