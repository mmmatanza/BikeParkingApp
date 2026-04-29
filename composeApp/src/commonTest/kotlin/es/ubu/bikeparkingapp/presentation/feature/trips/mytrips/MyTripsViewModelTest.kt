package es.ubu.bikeparkingapp.presentation.feature.trips.mytrips

import es.ubu.bikeparkingapp.domain.entity.ReservationState
import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.usecases.reservation.FakeCancelReservationUseCase
import es.ubu.bikeparkingapp.helper.usecases.reservation.FakeCheckInReservationUseCase
import es.ubu.bikeparkingapp.helper.usecases.reservation.FakeCheckOutReservationUseCase
import es.ubu.bikeparkingapp.helper.usecases.reservation.FakeExtendReservationUseCase
import es.ubu.bikeparkingapp.helper.usecases.reservation.FakeGetDetailedUserReservationsUseCase
import es.ubu.bikeparkingapp.helper.usecases.user.FakeGetUserIdUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class MyTripsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: MyTripsViewModel
    private lateinit var userIdUseCase: FakeGetUserIdUseCase
    private lateinit var getDetailedUserReservationsUseCase: FakeGetDetailedUserReservationsUseCase
    private lateinit var cancelReservationUseCase: FakeCancelReservationUseCase
    private lateinit var checkInReservationUseCase: FakeCheckInReservationUseCase
    private lateinit var checkOutReservationUseCase: FakeCheckOutReservationUseCase
    private lateinit var extendReservationUseCase: FakeExtendReservationUseCase

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userIdUseCase = FakeGetUserIdUseCase()
        getDetailedUserReservationsUseCase = FakeGetDetailedUserReservationsUseCase()
        cancelReservationUseCase = FakeCancelReservationUseCase()
        checkInReservationUseCase = FakeCheckInReservationUseCase()
        checkOutReservationUseCase = FakeCheckOutReservationUseCase()
        extendReservationUseCase = FakeExtendReservationUseCase()

        viewModel = MyTripsViewModel(
            userIdUseCase,
            getDetailedUserReservationsUseCase,
            cancelReservationUseCase,
            checkInReservationUseCase,
            checkOutReservationUseCase,
            extendReservationUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadTrips ordena las reservas por estado y luego por fecha correctamente`() = runTest(testDispatcher) {
        // Preparación
        val userId = "user1"
        val resCheckIn = TestData.testReservationDetail.copy(
            reservation = TestData.testReservation.copy(reservationId = "1", state = ReservationState.CHECKED_IN)
        )
        val resReserved = TestData.testReservationDetail.copy(
            reservation = TestData.testReservation.copy(reservationId = "2", state = ReservationState.RESERVED)
        )
        val resCancelled = TestData.testReservationDetail.copy(
            reservation = TestData.testReservation.copy(reservationId = "3", state = ReservationState.CANCELLED)
        )

        userIdUseCase.response = userId
        // Devolvemos la lista desordenada
        getDetailedUserReservationsUseCase.response = listOf(resCancelled, resReserved, resCheckIn)

        // Ejecución
        viewModel.loadTrips()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.state.value
        assertEquals("1", state.reservations[0].reservation.reservationId, "CHECKED_IN debería ir primero")
        assertEquals("2", state.reservations[1].reservation.reservationId, "RESERVED debería ir segundo")
        assertEquals("3", state.reservations[2].reservation.reservationId, "Otros (CANCELLED) deberían ir al final")
    }

    @Test
    fun `checkInReservation actualiza el estado de la reserva especifica en la lista`() = runTest(testDispatcher) {
        // Cargamos una lista inicial
        val resId = "res_to_checkin"
        val initialReservation = TestData.testReservationDetail.copy(
            reservation = TestData.testReservation.copy(reservationId = resId, state = ReservationState.RESERVED)
        )
        getDetailedUserReservationsUseCase.response = listOf(initialReservation)
        userIdUseCase.response = "id"
        viewModel.loadTrips()
        testDispatcher.scheduler.advanceUntilIdle()

        // Ejecutamos el Check-In
        viewModel.checkInReservationDialog(resId)
        viewModel.checkInReservation()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val updatedRes = viewModel.state.value.reservations.first { it.reservation.reservationId == resId }
        assertEquals(ReservationState.CHECKED_IN, updatedRes.reservation.state)
        assertFalse(viewModel.state.value.checkInReservationDialog)
    }

    @Test
    fun `cancelReservation cambia el estado a CANCELLED sin recargar toda la lista`() = runTest(testDispatcher) {
        // Preparación
        val resId = "cancel_me"
        getDetailedUserReservationsUseCase.response = listOf(
            TestData.testReservationDetail.copy(reservation = TestData.testReservation.copy(reservationId = resId))
        )
        userIdUseCase.response = "id"
        viewModel.loadTrips()
        testDispatcher.scheduler.advanceUntilIdle()

        // Ejecución
        viewModel.cancelReservationDialog(resId)
        viewModel.cancelReservation()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(ReservationState.CANCELLED, viewModel.state.value.reservations[0].reservation.state)
    }

    @Test
    fun `extendReservation aumenta 60 minutos a la hora de salida`() = runTest(testDispatcher) {
        // Preparación
        val resId = "extend_me"
        val originalOutTime = Instant.parse("2023-10-10T10:00:00Z")
        getDetailedUserReservationsUseCase.response = listOf(
            TestData.testReservationDetail.copy(reservation = TestData.testReservation.copy(reservationId = resId, outTime = originalOutTime))
        )
        userIdUseCase.response = "id"
        viewModel.loadTrips()
        testDispatcher.scheduler.advanceUntilIdle()

        // Ejecución
        viewModel.extendReservationDialog(resId)
        viewModel.extendReservation()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val newOutTime = viewModel.state.value.reservations[0].reservation.outTime
        val expectedTime = originalOutTime.plus(60, DateTimeUnit.MINUTE)
        assertEquals(expectedTime, newOutTime)
    }
}