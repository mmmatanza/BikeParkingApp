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

class CancelReservationUseCaseTest {

    private lateinit var reservationRepository: FakeReservationRepository
    private lateinit var useCase: CancelReservationUseCaseImpl

    @BeforeTest
    fun setUp() {
        reservationRepository = FakeReservationRepository()
        useCase = CancelReservationUseCaseImpl(reservationRepository)
    }

    @Test
    fun `Cancelacion exitosa cuando la reserva esta en estado RESERVED`() = runTest {
        // Preparación
        val resId = "res_123"
        // Creamos una reserva que permite cancelación
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
        assertEquals(ReservationState.CANCELLED, updatedRes.state)
    }

    @Test
    fun `Fallo cuando la reserva no existe`() = runTest {

        // Ejecución
        val result = useCase("id_inexistente")

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ReservationNotFoundException)
    }

    @Test
    fun `Fallo cuando la reserva esta en un estado que no permite cancelacion`() = runTest {
        // Preparación
        val resId = "res_finalizada"

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