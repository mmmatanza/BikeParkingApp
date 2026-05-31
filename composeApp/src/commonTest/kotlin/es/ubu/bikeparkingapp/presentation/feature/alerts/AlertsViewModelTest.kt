package es.ubu.bikeparkingapp.presentation.feature.alerts

import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.usecases.alert.FakeGetAlertsUseCase
import es.ubu.bikeparkingapp.helper.usecases.alert.FakeMarkAlertAsReadUseCase
import es.ubu.bikeparkingapp.helper.usecases.alert.FakeMarkAllAlertsAsReadUseCase
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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AlertsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: AlertsViewModel
    private lateinit var getAlertsUseCase: FakeGetAlertsUseCase
    private lateinit var markAlertAsReadUseCase: FakeMarkAlertAsReadUseCase
    private lateinit var markAllAlertsAsReadUseCase: FakeMarkAllAlertsAsReadUseCase

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getAlertsUseCase = FakeGetAlertsUseCase()
        markAlertAsReadUseCase = FakeMarkAlertAsReadUseCase()
        markAllAlertsAsReadUseCase = FakeMarkAllAlertsAsReadUseCase()

        viewModel = AlertsViewModel(
            getAlertsUseCase,
            markAlertAsReadUseCase,
            markAllAlertsAsReadUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadAlerts carga y ordena las alertas correctamente`() = runTest(testDispatcher) {
        val a1 = TestData.testAlert.copy(alertId = "a1", isRead = true, createdAt = TestData.testAlert.createdAt)
        val a2 = TestData.testAlert.copy(alertId = "a2", isRead = false, createdAt = TestData.testAlert.createdAt)
        val a3 = TestData.testAlert.copy(alertId = "a3", isRead = false, createdAt = TestData.testAlert.createdAt)
        
        getAlertsUseCase.response = listOf(a1, a2, a3)

        // Ejecución
        viewModel.loadAlerts()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(3, state.alerts.size)

        // Deben aparecer primero las no leídas
        assertFalse(state.alerts[0].isRead)
        assertFalse(state.alerts[1].isRead)
        assertTrue(state.alerts[2].isRead)
    }

    @Test
    fun `markAsRead actualiza el estado local tras el exito del caso de uso`() = runTest(testDispatcher) {
        // Preparación
        val alert = TestData.testAlert.copy(alertId = "a1", isRead = false)
        getAlertsUseCase.response = listOf(alert)
        viewModel.loadAlerts()
        testDispatcher.scheduler.advanceUntilIdle()

        // Ejecución
        viewModel.markAsRead("a1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue(viewModel.state.value.alerts.first { it.alertId == "a1" }.isRead)
    }

    @Test
    fun `markAllAsRead actualiza todas las alertas en el estado local`() = runTest(testDispatcher) {
        // Preparación
        val a1 = TestData.testAlert.copy(alertId = "a1", isRead = false)
        val a2 = TestData.testAlert.copy(alertId = "a2", isRead = false)
        getAlertsUseCase.response = listOf(a1, a2)
        viewModel.loadAlerts()
        testDispatcher.scheduler.advanceUntilIdle()

        // Ejecución
        viewModel.markAllAsRead()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue(viewModel.state.value.alerts.all { it.isRead })
    }

    @Test
    fun `error en loadAlerts actualiza el estado con el error mapeado`() = runTest(testDispatcher) {
        // Preparación
        getAlertsUseCase.shouldReturnError = true

        // Ejecución
        viewModel.loadAlerts()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertNotNull(viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
    }
}
