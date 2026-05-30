package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement

import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.usecases.occupancy.FakeGetPredictedOccupancyUseCase
import es.ubu.bikeparkingapp.helper.usecases.parking.FakeDeactivateParkingAreaUseCase
import es.ubu.bikeparkingapp.helper.usecases.parking.FakeGetParkingAreaByIdUseCase
import es.ubu.bikeparkingapp.helper.usecases.parking.FakeToggleOperativeStateUseCase
import es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.parkingmanagement.ParkingManagementViewModel
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
class ParkingManagementViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ParkingManagementViewModel
    private lateinit var getParkingAreaByIdUseCase: FakeGetParkingAreaByIdUseCase
    private lateinit var deactivateParkingAreaUseCase: FakeDeactivateParkingAreaUseCase
    private lateinit var toggleOperativeStateUseCase: FakeToggleOperativeStateUseCase
    private lateinit var getPredictedOccupancyUseCase: FakeGetPredictedOccupancyUseCase

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getParkingAreaByIdUseCase = FakeGetParkingAreaByIdUseCase()
        deactivateParkingAreaUseCase = FakeDeactivateParkingAreaUseCase()
        toggleOperativeStateUseCase = FakeToggleOperativeStateUseCase()
        getPredictedOccupancyUseCase = FakeGetPredictedOccupancyUseCase()

        viewModel = ParkingManagementViewModel(
            getParkingAreaByIdUseCase,
            deactivateParkingAreaUseCase,
            toggleOperativeStateUseCase,
            getPredictedOccupancyUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadParkingArea carga el parking correctamente`() = runTest(testDispatcher) {
        val parking = TestData.testParking.copy(parkingAreaId = "park_99")
        getParkingAreaByIdUseCase.response = parking

        viewModel.loadParkingArea("park_99")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(parking, viewModel.state.value.parking)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `onToggleConfirm cambia el estado operativo del parking con exito`() = runTest(testDispatcher) {
        // Preparación
        val initialParking = TestData.testParking.copy(isOperative = true)
        getParkingAreaByIdUseCase.response = initialParking
        viewModel.loadParkingArea("id")
        testDispatcher.scheduler.advanceUntilIdle()

        // Ejecución
        viewModel.onToggleConfirm()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertFalse(viewModel.state.value.parking!!.isOperative)
        assertFalse(viewModel.state.value.showToggleDialog)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `onDeactivateConfirm marca exito tras desactivacion`() = runTest(testDispatcher) {
        // Preparación
        getParkingAreaByIdUseCase.response = TestData.testParking
        viewModel.loadParkingArea("id")
        testDispatcher.scheduler.advanceUntilIdle()

        // Ejecución
        viewModel.onDeactivateConfirm()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue(viewModel.state.value.successDeactivation)
        assertFalse(viewModel.state.value.showDeactivateDialog)
    }

    @Test
    fun `Gestion de dialogos actualiza el estado correctamente`() {
        // Test rápido del estado de la UI
        viewModel.onDeactivateClick()
        assertTrue(viewModel.state.value.showDeactivateDialog)

        viewModel.onDeactivateDialogDismiss()
        assertFalse(viewModel.state.value.showDeactivateDialog)

        viewModel.onToggleServiceClick()
        assertTrue(viewModel.state.value.showToggleDialog)

        viewModel.onToggleServiceDismiss()
        assertFalse(viewModel.state.value.showToggleDialog)
    }
}