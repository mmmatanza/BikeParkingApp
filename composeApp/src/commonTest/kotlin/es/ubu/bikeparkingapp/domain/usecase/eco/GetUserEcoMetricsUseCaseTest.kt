package es.ubu.bikeparkingapp.domain.usecase.eco

import es.ubu.bikeparkingapp.domain.entity.UserEcoMetrics
import es.ubu.bikeparkingapp.domain.entity.UserPeriodMetrics
import es.ubu.bikeparkingapp.helper.repositories.FakeAuthRepository
import es.ubu.bikeparkingapp.helper.repositories.FakeEcoMetricsRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetUserEcoMetricsUseCaseTest {
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var ecoMetricsRepository: FakeEcoMetricsRepository
    private lateinit var getUserEcoMetricsUseCase: GetUserEcoMetricsUseCase

    @BeforeTest
    fun setup() {
        authRepository = FakeAuthRepository()
        ecoMetricsRepository = FakeEcoMetricsRepository()
        getUserEcoMetricsUseCase = GetUserEcoMetricsUseCaseImpl(authRepository, ecoMetricsRepository)
    }

    @Test
    fun `invoke devuelve las metricas cuando el usuario esta autenticado`() = runTest {
        authRepository.currentUserIdResult = Result.success("user123")
        val periodMetrics = UserPeriodMetrics(10.0, 1, 100)
        val metrics = UserEcoMetrics(periodMetrics, periodMetrics, periodMetrics)
        ecoMetricsRepository.userMetrics = metrics
        
        val result = getUserEcoMetricsUseCase()
        
        assertTrue(result.isSuccess)
        assertEquals(metrics, result.getOrThrow())
    }

    @Test
    fun `invoke falla cuando el usuario no esta autenticado`() = runTest {
        authRepository.currentUserIdResult = Result.failure(Exception("Not authenticated"))
        
        val result = getUserEcoMetricsUseCase()
        
        assertTrue(result.isFailure)
    }

    @Test
    fun `invoke falla cuando el repositorio falla`() = runTest {
        authRepository.currentUserIdResult = Result.success("user123")
        ecoMetricsRepository.shouldReturnError = true
        
        val result = getUserEcoMetricsUseCase()
        
        assertTrue(result.isFailure)
    }
}
