package es.ubu.bikeparkingapp.integration

import es.ubu.bikeparkingapp.domain.usecase.alert.GetAlertsUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.alert.MarkAlertAsReadUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.alert.MarkAllAlertsAsReadUseCaseImpl
import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.repositories.FakeAlertRepository
import es.ubu.bikeparkingapp.helper.repositories.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AlertIntegrationTest {

    private lateinit var alertRepository: FakeAlertRepository
    private lateinit var authRepository: FakeAuthRepository

    private lateinit var getAlertsUseCase: GetAlertsUseCaseImpl
    private lateinit var markAlertAsReadUseCase: MarkAlertAsReadUseCaseImpl
    private lateinit var markAllAlertsAsReadUseCase: MarkAllAlertsAsReadUseCaseImpl

    @BeforeTest
    fun setUp() {
        alertRepository = FakeAlertRepository()
        authRepository = FakeAuthRepository()

        getAlertsUseCase = GetAlertsUseCaseImpl(alertRepository, authRepository)
        markAlertAsReadUseCase = MarkAlertAsReadUseCaseImpl(alertRepository)
        markAllAlertsAsReadUseCase = MarkAllAlertsAsReadUseCaseImpl(alertRepository, authRepository)
    }

    @Test
    fun `Flujo completo de alertas - Obtener, marcar una y marcar todas`() = runTest {

        // Usuario autenticado con 2 alertas sin leer
        val userId = "user-123"
        authRepository.currentUserIdResult = Result.success(userId)
        
        val a1 = TestData.testAlert.copy(alertId = "a1", accountId = userId, isRead = false)
        val a2 = TestData.testAlert.copy(alertId = "a2", accountId = userId, isRead = false)
        alertRepository.alerts.addAll(listOf(a1, a2))


        // Recuperar alertas iniciales
        val initialAlerts = getAlertsUseCase().getOrThrow()
        assertEquals(2, initialAlerts.size, "Debería haber 2 alertas")
        assertTrue(initialAlerts.all { !it.isRead }, "Todas las alertas deberían estar sin leer")

        // Marcar una como leída
        markAlertAsReadUseCase("a1").getOrThrow()
        
        // Verificar estado intermedio
        val midAlerts = getAlertsUseCase().getOrThrow()
        assertEquals(
            midAlerts.find { it.alertId == "a1" }?.isRead,
            true,
            "La alerta a1 debería estar leída"
        )
        assertEquals(
            midAlerts.find { it.alertId == "a2" }?.isRead,
            false,
            "La alerta a2 debería seguir sin leer"
        )

        // Marcar todas como leídas
        markAllAlertsAsReadUseCase().getOrThrow()


        val finalAlerts = getAlertsUseCase().getOrThrow()
        assertTrue(finalAlerts.all { it.isRead }, "Todas las alertas del usuario deberían estar leídas")
    }

    @Test
    fun `Las acciones de alerta fallan si el usuario no esta autenticado`() = runTest {

        // Usuario no autenticado
        authRepository.currentUserIdResult = Result.failure(Exception("No session"))

        // Intentar obtener alertas
        val getResult = getAlertsUseCase()
        assertTrue(getResult.isFailure, "Obtener alertas debería fallar sin sesión")

        // Intentar marcar todas como leídas
        val markAllResult = markAllAlertsAsReadUseCase()
        assertTrue(markAllResult.isFailure, "Marcar todas debería fallar sin sesión")
    }
}
