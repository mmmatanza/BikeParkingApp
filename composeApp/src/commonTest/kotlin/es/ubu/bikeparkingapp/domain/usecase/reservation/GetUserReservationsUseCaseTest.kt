package es.ubu.bikeparkingapp.domain.usecase.reservation

import es.ubu.bikeparkingapp.domain.entity.ReservationState
import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.repositories.FakeReservationRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetUserReservationsUseCaseTest {

    private lateinit var reservationRepository: FakeReservationRepository
    private lateinit var useCase: GetUserReservationsUseCaseImpl

    @BeforeTest
    fun setUp() {
        reservationRepository = FakeReservationRepository()
        useCase = GetUserReservationsUseCaseImpl(reservationRepository)
    }

    @Test
    fun `Obtener reservas del usuario devuelve el historial completo`() = runTest {
        // Preparación
        val targetUserId = "user123"
        val otherUserId = "user456"

        // Reserva del usuario objetivo
        reservationRepository.save(
            TestData.testReservation.copy(
            reservationId = "res1",
            accountId = targetUserId,
            state = ReservationState.RESERVED
        ))

        // Reserva del usuario objetivo
        reservationRepository.save(TestData.testReservation.copy(
            reservationId = "res2",
            accountId = targetUserId,
            state = ReservationState.CHECKED_OUT
        ))

        // Reserva de otro usuario
        reservationRepository.save(TestData.testReservation.copy(
            reservationId = "res3",
            accountId = otherUserId
        ))

        // Ejecución
        val result = useCase(targetUserId)

        // Assert
        assertTrue(result.isSuccess)
        val reservations = result.getOrThrow()

        // Verificamos que solo trae las 2 del usuario indicado
        assertEquals(2, reservations.size)
        assertTrue(reservations.any { it.reservationId == "res1" })
        assertTrue(reservations.any { it.reservationId == "res2" })
    }

    @Test
    fun `Cuando el usuario no tiene reservas devuelve lista vacia`() = runTest {

        // Ejecución
        val result = useCase("nuevo_usuario")

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
    }
}