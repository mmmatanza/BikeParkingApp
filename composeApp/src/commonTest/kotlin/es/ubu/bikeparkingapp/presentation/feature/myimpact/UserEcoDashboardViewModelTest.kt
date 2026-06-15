package es.ubu.bikeparkingapp.presentation.feature.myimpact

import es.ubu.bikeparkingapp.domain.entity.UserEcoMetrics
import es.ubu.bikeparkingapp.domain.entity.UserPeriodMetrics
import es.ubu.bikeparkingapp.helper.usecases.eco.FakeGetUserEcoMetricsUseCase
import es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.ecodashboard.EcoPeriod
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
class UserEcoDashboardViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getUserEcoMetricsUseCase: FakeGetUserEcoMetricsUseCase
    private lateinit var viewModel: UserEcoDashboardViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getUserEcoMetricsUseCase = FakeGetUserEcoMetricsUseCase()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init carga las metricas del usuario correctamente`() = runTest {
        val periodMetrics = UserPeriodMetrics(10.0, 1, 100)
        val metrics = UserEcoMetrics(periodMetrics, periodMetrics, periodMetrics)
        getUserEcoMetricsUseCase.result = Result.success(metrics)
        
        viewModel = UserEcoDashboardViewModel(getUserEcoMetricsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(metrics, state.metrics)
        assertNull(state.error)
    }

    @Test
    fun `loadMetrics establece error al fallar`() = runTest {
        getUserEcoMetricsUseCase.result = Result.failure(Exception("Error al cargar métricas"))
        
        viewModel = UserEcoDashboardViewModel(getUserEcoMetricsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
    }

    @Test
    fun `onPeriodSelected actualiza el periodo seleccionado`() = runTest {
        getUserEcoMetricsUseCase.result = Result.failure(Exception("Ignore init"))
        viewModel = UserEcoDashboardViewModel(getUserEcoMetricsUseCase)
        
        viewModel.onPeriodSelected(EcoPeriod.MONTH)
        
        assertEquals(EcoPeriod.MONTH, viewModel.state.value.selectedPeriod)
    }
    
    private fun assertNull(actual: Any?) {
        assertEquals(actual, null, "Expected null but was $actual")
    }
}
