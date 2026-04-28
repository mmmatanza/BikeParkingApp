package es.ubu.bikeparkingapp.domain.usecase.reservation

import es.ubu.bikeparkingapp.domain.entity.ReservationState
import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.repositories.FakeReservationRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetParkingAreaActiveReservationsUseCaseTest {
    private lateinit var reservationRepository: FakeReservationRepository
    private lateinit var useCase: GetParkingAreaActiveReservationsUseCaseImpl

    @BeforeTest
    fun setUp() {
        reservationRepository = FakeReservationRepository()
        useCase = GetParkingAreaActiveReservationsUseCaseImpl(reservationRepository)
    }

    @Test
    fun `Obtener reservas activas devuelve solo las reservas del parking con estado activo`() = runTest {
        // Preparación
        val targetParkingId = "park_A"
        val otherParkingId = "park_B"

        // Reserva activa en el parking correcto
        reservationRepository.save(
            TestData.testReservation.copy(
            reservationId = "res1",
            parkingAreaId = targetParkingId,
            state = ReservationState.RESERVED
        ))

        // Reserva activa en otro parking
        reservationRepository.save(TestData.testReservation.copy(
            reservationId = "res2",
            parkingAreaId = otherParkingId,
            state = ReservationState.RESERVED
        ))

        // Reserva no activa en el parking correcto
        reservationRepository.save(TestData.testReservation.copy(
            reservationId = "res3",
            parkingAreaId = targetParkingId,
            state = ReservationState.CHECKED_OUT
        ))

        // Ejecución
        val result = useCase(targetParkingId)

        // Assert
        assertTrue(result.isSuccess)
        val activeList = result.getOrThrow()

        assertEquals(1, activeList.size, "Debería haber solo 1 reserva activa")
        assertEquals("res1", activeList[0].reservationId)
    }

    @Test
    fun `Cuando no hay reservas activas devuelve lista vacia`() = runTest {

        // Ejecución
        val result = useCase("cualquier_parking")

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
    }
}