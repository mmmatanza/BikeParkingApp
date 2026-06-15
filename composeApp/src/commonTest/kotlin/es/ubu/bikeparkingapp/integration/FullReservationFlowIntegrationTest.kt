package es.ubu.bikeparkingapp.integration

import es.ubu.bikeparkingapp.domain.entity.ReservationState
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingAreaByIdUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.reservation.AddReservationUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.reservation.CancelReservationUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.reservation.CheckInReservationUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.reservation.CheckOutReservationUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.reservation.ExtendReservationUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.reservation.GetDetailedUserReservationsUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.reservation.GetUserReservationsUseCaseImpl
import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.repositories.FakeAccountRepository
import es.ubu.bikeparkingapp.helper.repositories.FakeParkingAreaRepository
import es.ubu.bikeparkingapp.helper.repositories.FakeReservationRepository
import es.ubu.bikeparkingapp.helper.usecases.location.FakeGetUserLocationUseCase
import es.ubu.bikeparkingapp.helper.usecases.user.FakeGetUserIdUseCase
import es.ubu.bikeparkingapp.presentation.feature.parkingexplorer.parkingreservation.ParkingReservationViewModel
import es.ubu.bikeparkingapp.presentation.feature.trips.mytrips.MyTripsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FullReservationFlowIntegrationTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeReservationRepo: FakeReservationRepository
    private lateinit var fakeParkingRepo: FakeParkingAreaRepository
    private lateinit var fakeAccountRepo: FakeAccountRepository

    private val userId = "user_integration_test"
    private val parkingId = "parking_integration_test"

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeReservationRepo = FakeReservationRepository()
        fakeParkingRepo = FakeParkingAreaRepository()
        fakeAccountRepo = FakeAccountRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Flujo completo de reserva - Crear, Check-in y Check-out con puntos`() = runTest(testDispatcher) {
        val initialPoints = 10
        val parking = TestData.testParking.copy(
            parkingAreaId = parkingId,
            capacity = 10,
            currentOccupancy = 0,
            openingTime = "00:00",
            closingTime = "23:59",
            openDays = TestData.testParking.openDays // Incluye el día actual
        )
        fakeParkingRepo.addParkingArea(parking)
        fakeAccountRepo.getAccountResult = Result.success(TestData.testAccount.copy(accountId = userId, points = initialPoints))

        val getParkingByIdUseCase = GetParkingAreaByIdUseCaseImpl(fakeParkingRepo)
        val addReservationUseCase = AddReservationUseCaseImpl(fakeReservationRepo, fakeParkingRepo)
        val getUserIdUseCase = FakeGetUserIdUseCase().apply { response = userId }
        val getUserLocationUseCase = FakeGetUserLocationUseCase()

        val reservationViewModel = ParkingReservationViewModel(
            getParkingByIdUseCase,
            addReservationUseCase,
            getUserIdUseCase,
            getUserLocationUseCase
        )

        reservationViewModel.loadParkingArea(parkingId)
        testDispatcher.scheduler.advanceUntilIdle()
        
        reservationViewModel.addReservation()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(reservationViewModel.state.value.successfulReservation)
        val reservationId = "res_123"
        val reservation = fakeReservationRepo.reservations.first().copy(reservationId = reservationId)
        fakeReservationRepo.reservations[0] = reservation
        
        assertEquals(ReservationState.RESERVED, reservation.state)

        val getDetailedReservationsUseCase = GetDetailedUserReservationsUseCaseImpl(
            GetUserReservationsUseCaseImpl(fakeReservationRepo),
            getParkingByIdUseCase
        )
        
        val tripsViewModel = MyTripsViewModel(
            getUserIdUseCase,
            getDetailedReservationsUseCase,
            CancelReservationUseCaseImpl(fakeReservationRepo),
            CheckInReservationUseCaseImpl(fakeReservationRepo),
            CheckOutReservationUseCaseImpl(fakeReservationRepo, fakeAccountRepo),
            ExtendReservationUseCaseImpl(fakeReservationRepo)
        )

        tripsViewModel.loadTrips()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, tripsViewModel.state.value.reservations.size)

        tripsViewModel.checkInReservationDialog(reservationId)
        tripsViewModel.checkInReservation()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(ReservationState.CHECKED_IN, tripsViewModel.state.value.reservations[0].reservation.state)

        tripsViewModel.checkOutReservationDialog(reservationId)
        tripsViewModel.checkOutReservation()
        testDispatcher.scheduler.advanceUntilIdle()

        val finalReservation = fakeReservationRepo.findById(reservationId).getOrThrow()
        assertEquals(ReservationState.CHECKED_OUT, finalReservation.state)

        val finalAccount = fakeAccountRepo.getAccount(userId).getOrThrow()
        assertEquals(initialPoints + 2, finalAccount.points)
    }
}
