package es.ubu.bikeparkingapp.integration

import es.ubu.bikeparkingapp.domain.entity.ReservationState
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingAreaByIdUseCase
import es.ubu.bikeparkingapp.domain.usecase.reservation.CancelReservationUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.reservation.CheckInReservationUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.reservation.CheckOutReservationUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.reservation.ExtendReservationUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.reservation.GetDetailedUserReservationsUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.reservation.GetUserReservationsUseCase
import es.ubu.bikeparkingapp.domain.usecase.reservation.GetUserReservationsUseCaseImpl
import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.repositories.FakeAccountRepository
import es.ubu.bikeparkingapp.helper.repositories.FakeReservationRepository
import es.ubu.bikeparkingapp.helper.usecases.parking.FakeGetParkingAreaByIdUseCase
import es.ubu.bikeparkingapp.helper.usecases.user.FakeGetUserIdUseCase
import es.ubu.bikeparkingapp.presentation.feature.trips.mytrips.MyTripsViewModel
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
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class MyTripsIntegrationTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeReservationRepo: FakeReservationRepository
    private lateinit var fakeAccountRepo: FakeAccountRepository
    private lateinit var viewModel: MyTripsViewModel
    private lateinit var getUserReservationsUseCase: GetUserReservationsUseCase
    private lateinit var getParkingAreaByIdUseCase: GetParkingAreaByIdUseCase


    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeReservationRepo = FakeReservationRepository()
        fakeAccountRepo = FakeAccountRepository()
        getUserReservationsUseCase = GetUserReservationsUseCaseImpl(fakeReservationRepo)
        getParkingAreaByIdUseCase = FakeGetParkingAreaByIdUseCase()

        val getReservationsUseCase = GetDetailedUserReservationsUseCaseImpl(
            getUserReservationsUseCase = getUserReservationsUseCase,
            getParkingAreaByIdUseCase = getParkingAreaByIdUseCase
        )
        val cancelUseCase = CancelReservationUseCaseImpl(fakeReservationRepo)
        val checkInUseCase = CheckInReservationUseCaseImpl(fakeReservationRepo)
        val checkOutUseCase = CheckOutReservationUseCaseImpl(fakeReservationRepo, fakeAccountRepo)
        val extendUseCase = ExtendReservationUseCaseImpl(fakeReservationRepo)
        val userIdUseCase = FakeGetUserIdUseCase()
        userIdUseCase.response = "user123"

        fakeAccountRepo.getAccountResult = Result.success(TestData.testAccount.copy(accountId = "user123"))

        viewModel = MyTripsViewModel(
            userIdUseCase = userIdUseCase,
            getDetailedUserReservationsUseCase = getReservationsUseCase,
            cancelReservationUseCase = cancelUseCase,
            checkInReservationUseCase = checkInUseCase,
            checkOutReservationUseCase = checkOutUseCase,
            extendReservationUseCase = extendUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Cancelar reserva actualiza el estado en la lista correctamente`() =
        runTest(testDispatcher) {

            val resId = "res1"
            fakeReservationRepo.save(
                TestData.testReservation.copy(
                    reservationId = resId,
                    accountId = "user123"
                )
            )

            // Cargar las reservas en el ViewModel
            viewModel.loadTrips()
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(1, viewModel.state.value.reservations.size)
            assertEquals(
                ReservationState.RESERVED,
                viewModel.state.value.reservations[0].reservation.state
            )

            // Ejecutar la cancelación
            viewModel.cancelReservationDialog(resId)
            viewModel.cancelReservation()
            testDispatcher.scheduler.advanceUntilIdle()


            val finalState =
                viewModel.state.value.reservations.first { it.reservation.reservationId == resId }
            assertEquals(ReservationState.CANCELLED, finalState.reservation.state)
            assertNull(viewModel.state.value.error)
            assertFalse(viewModel.state.value.cancelReservationDialog)
        }

    @Test
    fun `Hacer Check-In cambia el estado a CHECKED_IN`() = runTest(testDispatcher) {
        val resId = "res_checkin"
        fakeReservationRepo.save(
            TestData.testReservation.copy(
                reservationId = resId,
                accountId = "user123",
                state = ReservationState.RESERVED
            )
        )

        viewModel.loadTrips()
        testDispatcher.scheduler.advanceUntilIdle()

        // Ejecutar Check-In
        viewModel.checkInReservationDialog(resId)
        viewModel.checkInReservation()
        testDispatcher.scheduler.advanceUntilIdle()

        // Verificar que el estado en la lista del ViewModel es el correcto
        val reservation =
            viewModel.state.value.reservations.first { it.reservation.reservationId == resId }
        assertEquals(ReservationState.CHECKED_IN, reservation.reservation.state)
        assertFalse(viewModel.state.value.checkInReservationDialog)
    }

    @Test
    fun `Extender reserva amplia 60 minutos correctamente`() = runTest(testDispatcher) {
        val resId = "res_extend"
        val originalOutTime = TestData.testReservation.outTime
        fakeReservationRepo.save(
            TestData.testReservation.copy(
                reservationId = resId,
                accountId = "user123",
                state = ReservationState.CHECKED_IN
            )
        )

        viewModel.loadTrips()
        testDispatcher.scheduler.advanceUntilIdle()

        // Abrir diálogo y ejecutar extensión
        viewModel.extendReservationDialog(resId)
        viewModel.extendReservation()
        testDispatcher.scheduler.advanceUntilIdle()

        // Verificar actualización en el State del ViewModel
        val updatedRes = viewModel.state.value.reservations.first { it.reservation.reservationId == resId }
        val expectedTime = originalOutTime.plus(60, DateTimeUnit.MINUTE)

        assertEquals(expectedTime, updatedRes.reservation.outTime)
        assertFalse(viewModel.state.value.extendReservationDialog)
    }

}