package es.ubu.bikeparkingapp.domain.usecase.parking

import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.repositories.FakeParkingAreaRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetParkingAreasUseCaseTest {
    private lateinit var repository: FakeParkingAreaRepository
    private lateinit var useCase: GetParkingAreasUseCaseImpl

    @BeforeTest
    fun setUp() {
        repository = FakeParkingAreaRepository()
        useCase = GetParkingAreasUseCaseImpl(repository)
    }

    @Test
    fun `Obtener parkings por dueño los devuelve ordenados por nombre`() = runTest {
        // Preparación
        val ownerId = "owner_1"
        // Añadimos parkings en desorden alfabético
        repository.addParkingArea(TestData.testParking.copy(name = "Zaragoza Parking", ownerId = ownerId))
        repository.addParkingArea(TestData.testParking.copy(name = "Abasto Central", ownerId = ownerId))
        repository.addParkingArea(TestData.testParking.copy(name = "Madrid Rio", ownerId = ownerId))

        // Ejecución
        val result = useCase(ownerId)

        // Assert
        assertTrue(result.isSuccess)
        val sortedList = result.getOrThrow()

        assertEquals(3, sortedList.size)
        assertEquals("Abasto Central", sortedList[0].name)
        assertEquals("Madrid Rio", sortedList[1].name)
        assertEquals("Zaragoza Parking", sortedList[2].name)
    }

    @Test
    fun `Cuando el dueño no tiene parkings devuelve lista vacia`() = runTest {
        // Ejecución
        val result = useCase("owner_sin_nada")

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
    }

    @Test
    fun `Cuando el repositorio falla se propaga el error`() = runTest {
        // Preparación
        repository.shouldReturnNetworkError = true

        // Ejecución
        val result = useCase("owner_id")

        // Assert
        assertTrue(result.isFailure)
    }
}