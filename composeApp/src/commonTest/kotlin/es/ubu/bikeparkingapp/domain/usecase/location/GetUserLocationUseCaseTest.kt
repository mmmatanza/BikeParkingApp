package es.ubu.bikeparkingapp.domain.usecase.location

import es.ubu.bikeparkingapp.domain.model.UserLocation
import es.ubu.bikeparkingapp.helper.repositories.FakeLocationRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetUserLocationUseCaseTest {

    private lateinit var repository: FakeLocationRepository
    private lateinit var useCase: GetUserLocationUseCaseImpl

    @BeforeTest
    fun setUp() {
        repository = FakeLocationRepository()
        useCase = GetUserLocationUseCaseImpl(repository)
    }

    @Test
    fun `Obtener ubicacion devuelve las coordenadas configuradas`() = runTest {
        // Preparación
        val expectedLocation = UserLocation(latitude = 42.3408, longitude = -3.6997)
        repository.locationToReturn = expectedLocation

        // Ejecución
        val result = useCase()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expectedLocation, result.getOrNull())
    }

    @Test
    fun `Fallo al obtener ubicacion devuelve un Result failure`() = runTest {
        // Preparación
        repository.shouldThrowException = true

        // Ejecución
        val result = useCase()

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Location error", result.exceptionOrNull()?.message)
    }
}