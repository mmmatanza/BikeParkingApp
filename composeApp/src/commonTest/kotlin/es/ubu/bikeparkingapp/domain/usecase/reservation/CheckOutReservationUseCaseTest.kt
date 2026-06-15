package es.ubu.bikeparkingapp.domain.usecase.reservation

import es.ubu.bikeparkingapp.domain.entity.ReservationState
import es.ubu.bikeparkingapp.domain.exception.InvalidReservationStateException
import es.ubu.bikeparkingapp.domain.exception.ReservationNotFoundException
import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.repositories.FakeAccountRepository
import es.ubu.bikeparkingapp.helper.repositories.FakeReservationRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CheckOutReservationUseCaseTest {

    private lateinit var reservationRepository: FakeReservationRepository
    private lateinit var accountRepository: FakeAccountRepository
    private lateinit var useCase: CheckOutReservationUseCaseImpl

    @BeforeTest
    fun setUp() {
        reservationRepository = FakeReservationRepository()
        accountRepository = FakeAccountRepository()
        useCase = CheckOutReservationUseCaseImpl(reservationRepository, accountRepository)
    }

    @Test
    fun `Check-out exitoso cuando la reserva esta en uso`() = runTest {

        val resId = "res_out_1"
        val userId = "user1"
        val initialPoints = 10
        // Una reserva en estado CHECKED_IN debería poder pasar a CHECKED_OUT
        reservationRepository.save(
            TestData.testReservation.copy(
            reservationId = resId,
            accountId = userId,
            state = ReservationState.CHECKED_IN
        ))
        accountRepository.getAccountResult = Result.success(TestData.testAccount.copy(accountId = userId, points = initialPoints))


        val result = useCase(resId)


        assertTrue(result.isSuccess)
        val updatedRes = reservationRepository.findById(resId).getOrThrow()
        assertEquals(ReservationState.CHECKED_OUT, updatedRes.state)
        assertEquals(initialPoints + 2, accountRepository.getAccount(userId).getOrThrow().points)
    }

    @Test
    fun `Fallo al hacer check-out si la reserva no existe`() = runTest {


        val result = useCase("res_no_existe")


        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ReservationNotFoundException)
    }

    @Test
    fun `Fallo al hacer check-out si la reserva ya estaba completada`() = runTest {

        val resId = "res_ya_finalizada"
        reservationRepository.save(TestData.testReservation.copy(
            reservationId = resId,
            state = ReservationState.CHECKED_OUT
        ))


        val result = useCase(resId)


        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InvalidReservationStateException)
    }
}