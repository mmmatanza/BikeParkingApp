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

class ReleaseReservationUseCaseTest {

    private lateinit var reservationRepository: FakeReservationRepository
    private lateinit var useCase: ReleaseReservationUseCaseImpl

    @BeforeTest
    fun setUp() {
        reservationRepository = FakeReservationRepository()
        useCase = ReleaseReservationUseCaseImpl(reservationRepository)
    }

    @Test
    fun `Liberacion exitosa cuando la reserva esta en un estado transicionable`() = runTest {
        // Preparación
        val resId = "res_liberar_1"
        // Simulamos una reserva que está en uso (CHECKED_IN)
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
    fun `Fallo al liberar si la reserva no existe`() = runTest {

        // Ejecución
        val result = useCase("id_inexistente")

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ReservationNotFoundException)
    }

    @Test
    fun `Fallo al liberar si la reserva ya estaba cancelada`() = runTest {
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