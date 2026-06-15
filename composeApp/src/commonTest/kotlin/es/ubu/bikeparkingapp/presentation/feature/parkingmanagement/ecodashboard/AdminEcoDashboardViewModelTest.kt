package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.ecodashboard

import es.ubu.bikeparkingapp.domain.entity.AdminEcoMetrics
import es.ubu.bikeparkingapp.helper.usecases.eco.FakeGetAdminEcoMetricsUseCase
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

@OptIn(ExperimentalCoroutinesApi::class)
class AdminEcoDashboardViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getAdminEcoMetricsUseCase: FakeGetAdminEcoMetricsUseCase
    private lateinit var viewModel: AdminEcoDashboardViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getAdminEcoMetricsUseCase = FakeGetAdminEcoMetricsUseCase()
        viewModel = AdminEcoDashboardViewModel(getAdminEcoMetricsUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadMetrics carga las metricas del administrador correctamente`() = runTest {
        val metrics = AdminEcoMetrics(
            weeklyDistance = 50.0,
            monthlyDistance = 200.0,
            yearlyDistance = 2000.0,
            weeklyTopUsers = emptyList(),
            monthlyTopUsers = emptyList(),
            yearlyTopUsers = emptyList()
        )
        getAdminEcoMetricsUseCase.result = Result.success(metrics)
        
        viewModel.loadMetrics("parking123")
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(metrics, state.metrics)
        assertNull(state.error)
    }

    @Test
    fun `loadMetrics establece error al fallar`() = runTest {
        getAdminEcoMetricsUseCase.result = Result.failure(Exception("Error en servidor"))
        
        viewModel.loadMetrics("parking123")
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
    }

    @Test
    fun `onPeriodSelected cambia el periodo en el estado`() {
        viewModel.onPeriodSelected(EcoPeriod.YEAR)
        assertEquals(EcoPeriod.YEAR, viewModel.state.value.selectedPeriod)
    }

    @Test
    fun `clearError limpia el error del estado`() = runTest {
        getAdminEcoMetricsUseCase.result = Result.failure(Exception("Error"))
        viewModel.loadMetrics("id")
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.clearError()
        
        assertNull(viewModel.state.value.error)
    }

    private fun assertNull(actual: Any?) {
        assertEquals(actual, null, "Expected null but was $actual")
    }
}
