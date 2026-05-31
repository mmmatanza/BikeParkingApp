package es.ubu.bikeparkingapp.domain.usecase.alert

import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.repositories.FakeAlertRepository
import es.ubu.bikeparkingapp.helper.repositories.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class MarkAllAlertsAsReadUseCaseTest {

    private lateinit var alertRepository: FakeAlertRepository
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var useCase: MarkAllAlertsAsReadUseCaseImpl

    @BeforeTest
    fun setUp() {
        alertRepository = FakeAlertRepository()
        authRepository = FakeAuthRepository()
        useCase = MarkAllAlertsAsReadUseCaseImpl(alertRepository, authRepository)
    }

    @Test
    fun `Marcar todas las alertas como leidas actualiza todas las alertas del usuario`() = runTest {
        // Preparación
        val userId = "user-123"
        authRepository.currentUserIdResult = Result.success(userId)
        
        alertRepository.alerts.add(TestData.testAlert.copy(alertId = "a1", accountId = userId, isRead = false))
        alertRepository.alerts.add(TestData.testAlert.copy(alertId = "a2", accountId = userId, isRead = false))
        alertRepository.alerts.add(TestData.testAlert.copy(alertId = "a3", accountId = "other", isRead = false))

        // Ejecución
        val result = useCase()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(alertRepository.alerts.find { it.alertId == "a1" }?.isRead, true)
        assertEquals(alertRepository.alerts.find { it.alertId == "a2" }?.isRead, true)
        assertNotEquals(alertRepository.alerts.find { it.alertId == "a3" }?.isRead, true)
    }

    @Test
    fun `Si el usuario no esta logueado devuelve error al marcar todas como leidas`() = runTest {
        authRepository.currentUserIdResult = Result.failure(Exception("Not logged in"))
        
        val result = useCase()
        
        assertTrue(result.isFailure)
    }
}
