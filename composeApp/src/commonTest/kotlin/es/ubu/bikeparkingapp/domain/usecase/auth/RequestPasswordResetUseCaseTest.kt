package es.ubu.bikeparkingapp.domain.usecase.auth

import es.ubu.bikeparkingapp.helper.repositories.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RequestPasswordResetUseCaseTest {

    private lateinit var repository: FakeAuthRepository
    private lateinit var useCase: RequestPasswordResetUseCaseImpl

    @BeforeTest
    fun setUp() {
        repository = FakeAuthRepository()
        useCase = RequestPasswordResetUseCaseImpl(repository)
    }

    @Test
    fun `Solicitud de reseteo exitosa`() = runTest {
        // Preparación
        repository.requestPasswordResetResult = Result.success(Unit)

        // Ejecución
        val result = useCase("test@example.com")

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `Fallo cuando el repositorio devuelve error`() = runTest {
        // Preparación
        val expectedError = Exception("User not found")
        repository.requestPasswordResetResult = Result.failure(expectedError)

        // Ejecución
        val result = useCase("error@example.com")

        // Assert
        assertTrue(result.isFailure)
        assertEquals(expectedError, result.exceptionOrNull())
    }
}