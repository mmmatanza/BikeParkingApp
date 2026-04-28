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

class CheckOutReservationUseCaseTest {

    private lateinit var reservationRepository: FakeReservationRepository
    private lateinit var useCase: CheckOutReservationUseCaseImpl

    @BeforeTest
    fun setUp() {
        reservationRepository = FakeReservationRepository()
        useCase = CheckOutReservationUseCaseImpl(reservationRepository)
    }

    @Test
    fun `Check-out exitoso cuando la reserva esta en uso`() = runTest {
        // Preparación
        val resId = "res_out_1"
        // Una reserva en estado CHECKED_IN debería poder pasar a CHECKED_OUT
        reservationRepository.save(
            TestData.testReservation.copy(
            reservationId = resId,
            state = ReservationState.CHECKED_IN
        ))

        // Ejecución
        val result = useCase(resId)

        // Assert
        assertTrue(result.isSuccess)
        val updatedRes = reservationRepository.findById(resId).getOrThrow()
        assertEquals(ReservationState.CHECKED_OUT, updatedRes.state)
    }

    @Test
    fun `Fallo al hacer check-out si la reserva no existe`() = runTest {

        // Ejecución
        val result = useCase("res_no_existe")

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ReservationNotFoundException)
    }

    @Test
    fun `Fallo al hacer check-out si la reserva ya estaba completada`() = runTest {
        // Preparación
        val resId = "res_ya_finalizada"
        reservationRepository.save(TestData.testReservation.copy(
            reservationId = resId,
            state = ReservationState.CHECKED_OUT
        ))

        // Ejecución
        val result = useCase(resId)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InvalidReservationStateException)
    }
}