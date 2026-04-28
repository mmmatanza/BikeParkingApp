package es.ubu.bikeparkingapp.domain.usecase.parking

import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.repositories.FakeParkingAreaRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ToggleOperativeStateUseCaseTest {

    private lateinit var repository: FakeParkingAreaRepository
    private lateinit var useCase: ToggleOperativeStateUseCaseImpl

    @BeforeTest
    fun setUp() {
        repository = FakeParkingAreaRepository()
        useCase = ToggleOperativeStateUseCaseImpl(repository)
    }

    @Test
    fun `Cambiar a no operativo actualiza el repositorio correctamente`() = runTest {
        // Preparación
        val parkingId = "p1"
        val parkingInicial = TestData.testParking.copy(parkingAreaId = parkingId, isOperative = true)
        repository.addParkingArea(parkingInicial)

        // Ejecución
        val result = useCase(parkingId, isOperative = false)

        // Assert
        assertTrue(result.isSuccess)
        val parkingFinal = repository.getParkingAreaById(parkingId).getOrNull()
        assertEquals(parkingFinal?.isOperative, false)
    }

    @Test
    fun `Cambiar a operativo un parking que no lo era`() = runTest {
        // Preparación
        val parkingId = "p1"
        repository.addParkingArea(TestData.testParking.copy(parkingAreaId = parkingId, isOperative = false))

        // Ejecución
        val result = useCase(parkingId, isOperative = true)

        // Assert
        assertTrue(result.isSuccess)
        val parkingFinal = repository.getParkingAreaById(parkingId).getOrNull()
        assertEquals(parkingFinal?.isOperative, true)
    }

    @Test
    fun `Fallo cuando el repositorio devuelve error`() = runTest {
        // Preparación
        repository.shouldReturnNetworkError = true

        // Ejecución
        val result = useCase("id", isOperative = true)

        // Assert
        assertTrue(result.isFailure)
    }

}