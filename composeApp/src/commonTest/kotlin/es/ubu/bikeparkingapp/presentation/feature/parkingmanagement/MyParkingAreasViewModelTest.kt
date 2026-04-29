package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement

import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.usecases.parking.FakeGetParkingAreasUseCase
import es.ubu.bikeparkingapp.helper.usecases.user.FakeGetUserIdUseCase
import es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.myparkingareas.MyParkingAreasViewModel
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
class MyParkingAreasViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: MyParkingAreasViewModel
    private lateinit var getParkingAreasUseCase: FakeGetParkingAreasUseCase
    private lateinit var getUserIdUseCase: FakeGetUserIdUseCase

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getParkingAreasUseCase = FakeGetParkingAreasUseCase()
        getUserIdUseCase = FakeGetUserIdUseCase()

        viewModel = MyParkingAreasViewModel(
            getParkingAreasUseCase,
            getUserIdUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadParkingAreas carga los parkings del usuario correctamente`() = runTest(testDispatcher) {
        // Preparación
        val userId = "owner123"
        val mockList = listOf(
            TestData.testParking.copy(parkingAreaId = "p1", name = "Parking Norte"),
            TestData.testParking.copy(parkingAreaId = "p2", name = "Parking Sur")
        )
        getUserIdUseCase.response = userId
        getParkingAreasUseCase.setParkingAreas(userId,mockList.toMutableList())

        // Ejecución
        viewModel.loadParkingAreas()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(2, state.parkingAreas?.size)
        assertEquals(2, state.filteredParkingAreas?.size)
        assertEquals("p1", state.parkingAreas?.first()?.parkingAreaId)
    }

    @Test
    fun `onSearchQueryChange filtra los parkings por nombre correctamente`() = runTest(testDispatcher) {
        // Preparación
        val mockList = listOf(
            TestData.testParking.copy(name = "Centro Ciudad"),
            TestData.testParking.copy(name = "Estacion"),
            TestData.testParking.copy(name = "Centro Comercial")
        )
        getUserIdUseCase.response = "any"
        getParkingAreasUseCase.setParkingAreas("any", mockList)
        viewModel.loadParkingAreas()
        testDispatcher.scheduler.advanceUntilIdle()

        // Ejecución
        viewModel.onSearchQueryChange("centro ")

        // Assert
        val state = viewModel.state.value
        assertEquals(2, state.filteredParkingAreas?.size)
        assertTrue(state.filteredParkingAreas!!.all { it.name.contains("Centro") })
        assertEquals("centro ", state.searchQuery)
    }

    @Test
    fun `loadParkingAreas maneja error al obtener el ID de usuario`() = runTest(testDispatcher) {
        // Preparación
        getUserIdUseCase.shouldFail = true

        // Ejecución
        viewModel.loadParkingAreas()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertNotNull(viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `clearError limpia el error del estado`() {
        // Preparación
        getUserIdUseCase.shouldFail = true
        viewModel.loadParkingAreas()

        // Ejecución
        viewModel.clearError()

        // Assert
        assertNull(viewModel.state.value.error)
    }
}