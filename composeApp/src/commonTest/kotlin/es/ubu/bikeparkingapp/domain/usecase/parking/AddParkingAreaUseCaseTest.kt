package es.ubu.bikeparkingapp.domain.usecase.parking

import es.ubu.bikeparkingapp.helper.repositories.FakeParkingAreaRepository
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DayOfWeek
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class AddParkingAreaUseCaseTest {

    private lateinit var repository: FakeParkingAreaRepository
    private lateinit var useCase: AddParkingAreaUseCaseImpl

    private suspend fun invokeUseCase() = useCase(
        ownerId = "user123",
        name = "Parking Central",
        address = "Calle Falsa 123",
        capacity = 10,
        openingTime = "08:00",
        closingTime = "20:00",
        latitude = 40.0,
        longitude = -3.0,
        rules = listOf("No mascotas"),
        openDays = setOf(DayOfWeek.MONDAY)
    )

    @BeforeTest
    fun setUp() {
        // Inicializamos el fake y el caso de uso antes de cada test
        repository = FakeParkingAreaRepository()
        useCase = AddParkingAreaUseCaseImpl(repository)
    }

    @Test
    fun `Add exitoso`() = runTest {

        val result = invokeUseCase()

        assertTrue(result.isSuccess)
    }

    @Test
    fun `Fallo de red`() = runTest {
        repository.shouldReturnNetworkError = true

        val result = invokeUseCase()

        assertTrue(result.isFailure)
    }

}