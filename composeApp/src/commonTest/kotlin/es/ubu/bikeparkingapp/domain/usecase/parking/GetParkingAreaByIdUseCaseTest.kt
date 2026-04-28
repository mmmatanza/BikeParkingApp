package es.ubu.bikeparkingapp.domain.usecase.parking

import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.repositories.FakeParkingAreaRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetParkingAreaByIdUseCaseTest {

    private lateinit var repository: FakeParkingAreaRepository
    private lateinit var useCase: GetParkingAreaByIdUseCaseImpl

    @BeforeTest
    fun setUp() {
        repository = FakeParkingAreaRepository()
        useCase = GetParkingAreaByIdUseCaseImpl(repository)
    }

    @Test
    fun `Obtener parking por ID devuelve el objeto correcto`() = runTest {
        // Preparación
        val parkingId = "target_id"
        val expectedParking = TestData.testParking.copy(parkingAreaId = parkingId)
        repository.addParkingArea(expectedParking)

        // Ejecución
        val result = useCase(parkingId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(parkingId, result.getOrNull()?.parkingAreaId)
        assertEquals(expectedParking.name, result.getOrNull()?.name)
    }

    @Test
    fun `Obtener parking por ID inexistente devuelve fallo`() = runTest {

        // Ejecución
        val result = useCase("id_que_no_existe")

        // Assert
        assertTrue(result.isFailure)
    }

    @Test
    fun `Fallo de red al buscar por ID devuelve error`() = runTest {
        // Preparación
        repository.shouldReturnNetworkError = true

        // Ejecución
        val result = useCase("cualquier_id")

        // Assert
        assertTrue(result.isFailure)
    }
}