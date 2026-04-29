package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement

import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.usecases.reservation.FakeCancelReservationUseCase
import es.ubu.bikeparkingapp.helper.usecases.reservation.FakeGetParkingAreaActiveReservationsUseCase
import es.ubu.bikeparkingapp.helper.usecases.reservation.FakeReleaseReservationUseCase
import es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.parkingreservations.ParkingReservationsViewModel
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ParkingReservationsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ParkingReservationsViewModel
    private lateinit var getParkingReservationsUseCase: FakeGetParkingAreaActiveReservationsUseCase
    private lateinit var cancelReservationUseCase: FakeCancelReservationUseCase
    private lateinit var releaseReservationUseCase: FakeReleaseReservationUseCase

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getParkingReservationsUseCase = FakeGetParkingAreaActiveReservationsUseCase()
        cancelReservationUseCase = FakeCancelReservationUseCase()
        releaseReservationUseCase = FakeReleaseReservationUseCase()

        viewModel = ParkingReservationsViewModel(
            getParkingReservationsUseCase,
            cancelReservationUseCase,
            releaseReservationUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadReservations carga la lista correctamente`() = runTest(testDispatcher) {
        // Preparación
        val mockReservations = listOf(
            TestData.testReservation.copy(reservationId = "res_1"),
            TestData.testReservation.copy(reservationId = "res_2")
        )
        getParkingReservationsUseCase.response = mockReservations

        // Ejecución
        viewModel.loadReservations("park_1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(2, state.reservations.size)
        assertEquals("res_1", state.reservations[0].reservationId)
    }

    @Test
    fun `cancelReservation elimina la reserva de la lista local tras exito`() = runTest(testDispatcher) {
        // Preparación
        val resIdToCancel = "res_1"
        val mockReservations = listOf(
            TestData.testReservation.copy(reservationId = resIdToCancel),
            TestData.testReservation.copy(reservationId = "res_2")
        )
        getParkingReservationsUseCase.response = mockReservations

        // Cargamos las reservas a través del ViewModel
        viewModel.loadReservations("any_id")
        testDispatcher.scheduler.advanceUntilIdle()

        // Simulamos que el usuario hace clic en cancelar
        viewModel.showCancelReservationDialog(resIdToCancel)

        // Ejecución
        viewModel.cancelReservation()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.state.value
        assertEquals(1, state.reservations.size)
        assertNull(state.reservations.find { it.reservationId == resIdToCancel })
    }

    @Test
    fun `releaseReservation elimina la reserva de la lista local tras exito`() = runTest(testDispatcher) {
        // Preparación
        val resIdToRelease = "res_release_99"
        getParkingReservationsUseCase.response = listOf(
            TestData.testReservation.copy(reservationId = "res_normal"),
            TestData.testReservation.copy(reservationId = resIdToRelease)
        )

        viewModel.loadReservations("park_123")
        testDispatcher.scheduler.advanceUntilIdle()

        // Acción de UI
        viewModel.showReleaseReservationDialog(resIdToRelease)

        // Ejecución
        viewModel.releaseReservation()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.state.value
        assertEquals(1, state.reservations.size)
        assertNull(state.reservations.find { it.reservationId == resIdToRelease })
        assertFalse(state.showReleaseReservationDialog)
        assertNull(state.reservationId)
    }

    @Test
    fun `Muestra y oculta dialogos de cancelacion correctamente`() {
        val id = "test_id"

        viewModel.showCancelReservationDialog(id)
        assertTrue(viewModel.state.value.showCancelReservationDialog)
        assertEquals(id, viewModel.state.value.reservationId)

        viewModel.dismissCancelReservationDialog()
        assertFalse(viewModel.state.value.showCancelReservationDialog)
        assertNull(viewModel.state.value.reservationId)
    }
}