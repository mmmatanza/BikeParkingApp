package es.ubu.bikeparkingapp.domain.usecase.parking

import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.repositories.FakeParkingAreaRepository
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DayOfWeek
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UpdateParkingAreaUseCaseTest {
    private lateinit var repository: FakeParkingAreaRepository
    private lateinit var useCase: UpdateParkingAreaUseCaseImpl

    @BeforeTest
    fun setUp() {
        repository = FakeParkingAreaRepository()
        useCase = UpdateParkingAreaUseCaseImpl(repository)
    }

    @Test
    fun `Actualizacion exitosa guarda los nuevos datos en el repositorio`() = runTest {
        // Preparación
        val parkingId = "park_123"
        val ownerId = "owner_456"
        // Insertamos el estado inicial en el fake
        repository.addParkingArea(TestData.testParking.copy(parkingAreaId = parkingId, name = "Nombre Antiguo"))

        // Ejecución
        val result = useCase(
            parkingAreaId = parkingId,
            ownerId = ownerId,
            name = "Nombre Nuevo",
            address = "Calle Nueva 1",
            capacity = 50,
            openingTime = "09:00",
            closingTime = "21:00",
            latitude = 41.0,
            longitude = -2.0,
            rules = listOf("Solo bicis"),
            openDays = setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY)
        )

        // Assert
        assertTrue(result.isSuccess)

        // Verificamos que los datos en el repositorio sean los nuevos
        val updatedParking = repository.getParkingAreaById(parkingId).getOrNull()
        assertEquals("Nombre Nuevo", updatedParking?.name)
        assertEquals("Calle Nueva 1", updatedParking?.address)
        assertEquals(50, updatedParking?.capacity)
        assertTrue(updatedParking?.openDays?.contains(DayOfWeek.MONDAY) == true)
    }

    @Test
    fun `Fallo de red al actualizar devuelve error`() = runTest {
        // Preparación
        repository.shouldReturnNetworkError = true

        // Ejecución
        val result = useCase(
            parkingAreaId = "any",
            ownerId = "any",
            name = "any",
            address = "any",
            capacity = 10,
            openingTime = "00:00",
            closingTime = "23:59",
            latitude = 0.0,
            longitude = 0.0,
            rules = emptyList(),
            openDays = emptySet()
        )

        // Assert
        assertTrue(result.isFailure)
    }
}