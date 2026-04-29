package es.ubu.bikeparkingapp.domain.usecase.reservation

import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.usecases.parking.FakeGetParkingAreaByIdUseCase
import es.ubu.bikeparkingapp.helper.usecases.reservation.FakeGetUserReservationsUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetDetailedUserReservationsUseCaseTest {

    private lateinit var useCase: GetDetailedUserReservationsUseCaseImpl
    private lateinit var fakeGetUserReservationsUseCase: FakeGetUserReservationsUseCase
    private lateinit var fakeGetParkingAreaByIdUseCase: FakeGetParkingAreaByIdUseCase

    @BeforeTest
    fun setUp() {
        // Instanciamos los casos de uso de los que depende nuestro caso de uso complejo
        fakeGetUserReservationsUseCase = FakeGetUserReservationsUseCase()
        fakeGetParkingAreaByIdUseCase = FakeGetParkingAreaByIdUseCase()

        // Instanciamos nuestro caso de uso
        useCase = GetDetailedUserReservationsUseCaseImpl(
            getUserReservationsUseCase = fakeGetUserReservationsUseCase,
                getParkingAreaByIdUseCase = fakeGetParkingAreaByIdUseCase
        )
    }

    @Test
    fun `Resultado correcto con reservas y parking`() = runTest {
        // Preparamos 2 reservas y el parking que devolverá el fake
        val reserva1 = TestData.testReservation.copy(reservationId = "res1")
        val reserva2 = TestData.testReservation.copy(reservationId = "res2")

        // Configuramos las respuestas de los fake
        fakeGetUserReservationsUseCase.response = listOf(reserva1, reserva2)
        fakeGetParkingAreaByIdUseCase.response = TestData.testParking.copy(
            parkingAreaId = "park1",
            name = "Parking Central"
        )

        // Ejecución del caso de uso
        val result = useCase("user123")

        // Assert
        assertTrue(result.isSuccess)
        val details = result.getOrThrow()
        assertEquals(2, details.size)
        assertEquals("Parking Central", details[0].parkingName)
        assertEquals("user123", details[0].reservation.accountId)
    }

    @Test
    fun `Cuando el parking no se encuentra, devuelve detalles con campos vacios`() = runTest {
        // Preparamos una reserva de respuesta
        fakeGetUserReservationsUseCase.response = listOf(
            TestData.testReservation
        )
        fakeGetParkingAreaByIdUseCase.response = null

        // Ejecución del caso de uso
        val result = useCase("user123")

        // Assert
        assertTrue(result.isSuccess)
        val details = result.getOrThrow()
        assertEquals("", details[0].parkingName)
        assertEquals(0.0, details[0].parkingLatitude)
    }

    @Test
    fun `Cuando falla la carga de reservas, devuelve error`() = runTest {
        // Configuramos el fake para que falle la carga de reservas
        fakeGetUserReservationsUseCase.shouldFail = true
        fakeGetUserReservationsUseCase.exception = Exception("Error de servidor")

        // Ejecución del caso de uso
        val result = useCase("user123")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Error de servidor", result.exceptionOrNull()?.message)
    }

}