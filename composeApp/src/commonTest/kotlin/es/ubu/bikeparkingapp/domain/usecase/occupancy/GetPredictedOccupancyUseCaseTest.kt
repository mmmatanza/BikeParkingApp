package es.ubu.bikeparkingapp.domain.usecase.occupancy

import es.ubu.bikeparkingapp.helper.repositories.FakeOccupancyRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetPredictedOccupancyUseCaseTest {

    private lateinit var repository: FakeOccupancyRepository
    private lateinit var useCase: GetPredictedOccupancyUseCaseImpl

    @BeforeTest
    fun setUp() {
        repository = FakeOccupancyRepository()
        useCase = GetPredictedOccupancyUseCaseImpl(repository)
    }

    @Test
    fun `Obtener ocupacion predicha devuelve el valor del repositorio`() = runTest {
        repository.response = 5
        val result = useCase("park1")
        
        assertTrue(result.isSuccess)
        assertEquals(5, result.getOrThrow())
    }

    @Test
    fun `Fallo en el repositorio devuelve error`() = runTest {
        repository.shouldReturnError = true
        val result = useCase("park1")
        
        assertTrue(result.isFailure)
    }
}
