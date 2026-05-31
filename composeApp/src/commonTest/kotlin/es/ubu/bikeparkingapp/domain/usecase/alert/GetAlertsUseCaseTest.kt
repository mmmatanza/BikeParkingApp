package es.ubu.bikeparkingapp.domain.usecase.alert

import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.repositories.FakeAlertRepository
import es.ubu.bikeparkingapp.helper.repositories.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetAlertsUseCaseTest {

    private lateinit var alertRepository: FakeAlertRepository
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var useCase: GetAlertsUseCaseImpl

    @BeforeTest
    fun setUp() {
        alertRepository = FakeAlertRepository()
        authRepository = FakeAuthRepository()
        useCase = GetAlertsUseCaseImpl(alertRepository, authRepository)
    }

    @Test
    fun `Obtener alertas devuelve la lista del repositorio para el usuario logueado`() = runTest {
        // Preparación
        val userId = "user-123"
        authRepository.currentUserIdResult = Result.success(userId)
        alertRepository.alerts.add(TestData.testAlert.copy(alertId = "a1", accountId = userId))
        alertRepository.alerts.add(TestData.testAlert.copy(alertId = "a2", accountId = "other"))

        // Ejecución
        val result = useCase()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().size)
        assertEquals("a1", result.getOrThrow()[0].alertId)
    }

    @Test
    fun `Si el usuario no esta logueado devuelve error`() = runTest {
        authRepository.currentUserIdResult = Result.failure(Exception("Not logged in"))
        val result = useCase()
        
        assertTrue(result.isFailure)
    }
}
