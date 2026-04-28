package es.ubu.bikeparkingapp.domain.usecase.parking

import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.repositories.FakeParkingAreaRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetNearbyParkingAreasUseCaseTest {

    private lateinit var repository: FakeParkingAreaRepository
    private lateinit var useCase: GetNearbyParkingAreasUseCaseImpl

    @BeforeTest
    fun setUp() {
        repository = FakeParkingAreaRepository()
        useCase = GetNearbyParkingAreasUseCaseImpl(repository)
    }

    @Test
    fun `Obtener parkings cercanos devuelve la lista del repositorio`() = runTest {
        // Preparación
        val lat = 40.4167
        val lon = -3.7033
        val dist = 5.0

        // Preparamos el repositorio con un par de parkings
        repository.addParkingArea(TestData.testParking.copy(parkingAreaId = "p1", isActive = true, isOperative = true))
        repository.addParkingArea(TestData.testParking.copy(parkingAreaId = "p2", isActive = true, isOperative = true))

        // Ejecución
        val result = useCase(lat, lon, dist)

        // Assert
        assertTrue(result.isSuccess)
        val list = result.getOrThrow()
        assertEquals(2, list.size)
    }

    @Test
    fun `Fallo de red devuelve error en la busqueda cercana`() = runTest {
        // Preparación
        repository.shouldReturnNetworkError = true

        // Ejecución
        val result = useCase(0.0, 0.0, 10.0)

        // Assert
        assertTrue(result.isFailure)
    }
}