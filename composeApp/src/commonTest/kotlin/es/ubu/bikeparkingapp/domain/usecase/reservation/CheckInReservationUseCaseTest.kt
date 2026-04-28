package es.ubu.bikeparkingapp.domain.usecase.reservation

import es.ubu.bikeparkingapp.domain.entity.ReservationState
import es.ubu.bikeparkingapp.domain.exception.InvalidReservationStateException
import es.ubu.bikeparkingapp.domain.exception.ReservationNotFoundException
import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.repositories.FakeReservationRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CheckInReservationUseCaseTest {

    private lateinit var reservationRepository: FakeReservationRepository
    private lateinit var useCase: CheckInReservationUseCaseImpl

    @BeforeTest
    fun setUp() {
        reservationRepository = FakeReservationRepository()
        useCase = CheckInReservationUseCaseImpl(reservationRepository)
    }

    @Test
    fun `Check-in exitoso cuando la reserva esta en estado RESERVED`() = runTest {
        // Preparación
        val resId = "res_abc"
        reservationRepository.save(
            TestData.testReservation.copy(
            reservationId = resId,
            state = ReservationState.RESERVED
        ))

        // Ejecución
        val result = useCase(resId)

        // Assert
        assertTrue(result.isSuccess)
        val updatedRes = reservationRepository.findById(resId).getOrThrow()
        assertEquals(ReservationState.CHECKED_IN, updatedRes.state)
    }

    @Test
    fun `Fallo al hacer check-in si la reserva no existe`() = runTest {

        // Ejecución
        val result = useCase("id_fantasma")

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ReservationNotFoundException)
    }

    @Test
    fun `Fallo al hacer check-in si la reserva ya fue cancelada`() = runTest {
        // Preparación
        val resId = "res_cancelada"
        reservationRepository.save(TestData.testReservation.copy(
            reservationId = resId,
            state = ReservationState.CANCELLED
        ))

        // Ejecución
        val result = useCase(resId)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InvalidReservationStateException)
    }
}