package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement

import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.usecases.parking.FakeAddParkingAreaUseCase
import es.ubu.bikeparkingapp.helper.usecases.parking.FakeGetParkingAreaByIdUseCase
import es.ubu.bikeparkingapp.helper.usecases.parking.FakeUpdateParkingAreaUseCase
import es.ubu.bikeparkingapp.helper.usecases.user.FakeGetUserIdUseCase
import es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.upsertparkingarea.UpsertParkingAreaViewModel
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class UpsertParkingAreaViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: UpsertParkingAreaViewModel
    private lateinit var addParkingAreaUseCase: FakeAddParkingAreaUseCase
    private lateinit var updateParkingAreaUseCase: FakeUpdateParkingAreaUseCase
    private lateinit var getUserIdUseCase: FakeGetUserIdUseCase
    private lateinit var getParkingAreaByIdUseCase: FakeGetParkingAreaByIdUseCase

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        addParkingAreaUseCase = FakeAddParkingAreaUseCase()
        updateParkingAreaUseCase = FakeUpdateParkingAreaUseCase()
        getUserIdUseCase = FakeGetUserIdUseCase()
        getParkingAreaByIdUseCase = FakeGetParkingAreaByIdUseCase()

        viewModel = UpsertParkingAreaViewModel(
            addParkingAreaUseCase,
            updateParkingAreaUseCase,
            getUserIdUseCase,
            getParkingAreaByIdUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `validateForm devuelve falso si faltan datos basicos`() {
        viewModel.onNameChange("")
        viewModel.onAddressChange("Calle Falsa 123")
        assertFalse(viewModel.validateForm(), "Debería fallar por nombre vacío")
    }

    @Test
    fun `validateForm devuelve falso si la hora de cierre es anterior a la de apertura`() {
        viewModel.onNameChange("Parking Test")
        viewModel.onAddressChange("Dirección")
        viewModel.onCapacityChange(10)
        viewModel.onLocationChange(40.0, -3.0)

        viewModel.onOpeningTimeChange("18:00")
        viewModel.onClosingTimeChange("09:00")

        assertFalse(viewModel.validateForm(), "Cierre antes de apertura debería ser inválido")
    }

    @Test
    fun `onSaveParkingArea llama a AddUseCase cuando no esta editando`() = runTest(testDispatcher) {
        // Preparación
        getUserIdUseCase.response = "user_owner"
        viewModel.onNameChange("Nuevo Parking")
        viewModel.onAddressChange("Cualquiera")
        viewModel.onCapacityChange(50)
        viewModel.onLocationChange(40.0, -3.0)

        // Ejecución
        viewModel.onSaveParkingArea()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue(viewModel.state.value.isSuccess)
    }

    @Test
    fun `loadParkingArea carga correctamente los datos para editar`() = runTest(testDispatcher) {
        val parking = TestData.testParking.copy(parkingAreaId = "park_id_edit", name = "Editable")
        getParkingAreaByIdUseCase.response = parking

        viewModel.loadParkingArea("park_id_edit")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.isEditing)
        assertEquals("park_id_edit", state.parkingAreaId)
        assertEquals("Editable", state.name)
    }

    @Test
    fun `onSaveParkingArea llama a UpdateUseCase cuando esta editando`() = runTest(testDispatcher) {
        // Cargamos una zona existente
        getParkingAreaByIdUseCase.response = TestData.testParking.copy(parkingAreaId = "id_1")
        viewModel.loadParkingArea("id_1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Simulamos cambio y guardado
        getUserIdUseCase.response = "owner_id"
        viewModel.onNameChange("Nombre Actualizado")
        viewModel.onSaveParkingArea()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue(viewModel.state.value.isSuccess)
    }

    @Test
    fun `Gestion de reglas introduce y elimina correctamente`() {
        viewModel.onRuleInputChange("Nueva Regla")
        viewModel.onAddRule()

        assertEquals(1, viewModel.state.value.rules.size)
        assertEquals("Nueva Regla", viewModel.state.value.rules[0])
        assertEquals("", viewModel.state.value.currentRuleInput)

        viewModel.onRemoveRule(0)
        assertTrue(viewModel.state.value.rules.isEmpty())
    }

    @Test
    fun `onOpen24HoursToggle configura las horas automaticamente`() {
        viewModel.onOpen24HoursToggle(true)
        assertEquals("00:00", viewModel.state.value.openingTime)
        assertEquals("23:59", viewModel.state.value.closingTime)
    }
}