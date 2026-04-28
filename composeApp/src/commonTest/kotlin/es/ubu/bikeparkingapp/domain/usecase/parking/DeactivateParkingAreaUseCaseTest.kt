package es.ubu.bikeparkingapp.domain.usecase.parking

import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.repositories.FakeParkingAreaRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeactivateParkingAreaUseCaseTest {

    private lateinit var repository: FakeParkingAreaRepository
    private lateinit var useCase: DeactivateParkingAreaUseCaseImpl

    @BeforeTest
    fun setUp() {
        repository = FakeParkingAreaRepository()
        useCase = DeactivateParkingAreaUseCaseImpl(repository)
    }

    @Test
    fun `Desactivacion exitosa cambia el estado en el repositorio`() = runTest {

        // Añadimos un parking activo al repositorio fake
        val parkingId = "parking_001"
        val parkingInicial = TestData.testParking.copy(
            parkingAreaId = parkingId,
            isActive = true
        )
        // Usamos addParkingArea para poblar el fake
        repository.addParkingArea(parkingInicial)

        // Ejecución del caso de uso
        val result = useCase(parkingId)

        // Assert
        assertTrue(result.isSuccess)

        // Verificación extra
        val parkingFinal = repository.getParkingAreaById(parkingId).getOrNull()
        assertEquals(parkingFinal?.isActive, false)
    }

    @Test
    fun `Fallo al desactivar cuando hay error de red`() = runTest {
        // Preparamos el fake para que devuelva error de red
        repository.shouldReturnNetworkError = true

        // Ejecución del caso de uso
        val result = useCase("cualquier_id")

        // Assert
        assertTrue(result.isFailure)
    }
}