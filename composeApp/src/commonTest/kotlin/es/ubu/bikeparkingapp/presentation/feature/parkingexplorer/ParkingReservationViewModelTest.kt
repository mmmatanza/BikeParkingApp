package es.ubu.bikeparkingapp.presentation.feature.parkingexplorer

import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.usecases.location.FakeGetUserLocationUseCase
import es.ubu.bikeparkingapp.helper.usecases.parking.FakeGetParkingAreaByIdUseCase
import es.ubu.bikeparkingapp.helper.usecases.reservation.FakeAddReservationUseCase
import es.ubu.bikeparkingapp.helper.usecases.user.FakeGetUserIdUseCase
import es.ubu.bikeparkingapp.presentation.feature.parkingexplorer.parkingreservation.ParkingReservationViewModel
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
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ParkingReservationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ParkingReservationViewModel
    private lateinit var getParkingAreaByIdUseCase: FakeGetParkingAreaByIdUseCase
    private lateinit var addReservationUseCase: FakeAddReservationUseCase
    private lateinit var getUserIdUseCase: FakeGetUserIdUseCase
    private lateinit var getUserLocationUseCase: FakeGetUserLocationUseCase

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getParkingAreaByIdUseCase = FakeGetParkingAreaByIdUseCase()
        addReservationUseCase = FakeAddReservationUseCase()
        getUserIdUseCase = FakeGetUserIdUseCase()
        getUserLocationUseCase = FakeGetUserLocationUseCase()

        viewModel = ParkingReservationViewModel(
            getParkingAreaByIdUseCase,
            addReservationUseCase,
            getUserIdUseCase,
            getUserLocationUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadParkingArea actualiza el estado con el parking correcto`() = runTest(testDispatcher) {

        val parking = TestData.testParking.copy(parkingAreaId = "park_123")
        getParkingAreaByIdUseCase.response = parking


        viewModel.loadParkingArea("park_123")
        testDispatcher.scheduler.advanceUntilIdle()


        assertEquals(parking, viewModel.state.value.parkingArea)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `availableParking devuelve true solo si hay espacio y esta operativo`() = runTest(testDispatcher) {
        // Caso 1: Parking lleno
        val fullParking = TestData.testParking.copy(capacity = 10, currentOccupancy = 10, isOperative = true, isActive = true)
        getParkingAreaByIdUseCase.response = fullParking
        viewModel.loadParkingArea("full")
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.availableParking(), "No debería haber espacio si occupancy == capacity")

        // Caso 2: Parking no operativo
        val inoperativeParking = TestData.testParking.copy(capacity = 10, currentOccupancy = 0, isOperative = false, isActive = true)
        getParkingAreaByIdUseCase.response = inoperativeParking
        viewModel.loadParkingArea("inop")
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.availableParking(), "No debería estar disponible si isOperative es false")

        // Caso 3: Ok
        val okParking = TestData.testParking.copy(capacity = 10, currentOccupancy = 5, isOperative = true, isActive = true)
        getParkingAreaByIdUseCase.response = okParking
        viewModel.loadParkingArea("ok")
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.availableParking())
    }

    @Test
    fun `addReservation completa la reserva con exito`() = runTest(testDispatcher) {

        val parkingId = "park1"
        val userId = "user123"
        getParkingAreaByIdUseCase.response = TestData.testParking.copy(parkingAreaId = parkingId)
        getUserIdUseCase.response = userId

        viewModel.loadParkingArea(parkingId)
        testDispatcher.scheduler.advanceUntilIdle()


        viewModel.addReservation()
        testDispatcher.scheduler.advanceUntilIdle()


        assertTrue(viewModel.state.value.successfulReservation)
        assertFalse(viewModel.state.value.confirmReservationDialog)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `addReservation falla si el caso de uso devuelve error`() = runTest(testDispatcher) {

        getParkingAreaByIdUseCase.response = TestData.testParking
        getUserIdUseCase.response = "user123"
        addReservationUseCase.shouldFail = true

        viewModel.loadParkingArea("id")
        testDispatcher.scheduler.advanceUntilIdle()


        viewModel.addReservation()
        testDispatcher.scheduler.advanceUntilIdle()


        assertNotNull(viewModel.state.value.error)
        assertFalse(viewModel.state.value.successfulReservation)
    }
}