Spackage es.ubu.bikeparkingapp.integration

import es.ubu.bikeparkingapp.domain.usecase.parking.AddParkingAreaUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.parking.UpdateParkingAreaUseCaseImpl
import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.repositories.FakeAccountRepository
import es.ubu.bikeparkingapp.helper.repositories.FakeParkingAreaRepository
import es.ubu.bikeparkingapp.helper.usecases.parking.FakeGetParkingAreaByIdUseCase
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ParkingManagementIntegrationTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeParkingRepo: FakeParkingAreaRepository
    private lateinit var fakeAccountRepository: FakeAccountRepository
    private lateinit var addUseCase: AddParkingAreaUseCaseImpl
    private lateinit var updateUseCase: UpdateParkingAreaUseCaseImpl
    private lateinit var getUserIdUseCase: FakeGetUserIdUseCase
    private lateinit var getParkingAreaByIdUseCase: FakeGetParkingAreaByIdUseCase
    private lateinit var viewModel: UpsertParkingAreaViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeAccountRepository = FakeAccountRepository()
        fakeParkingRepo = FakeParkingAreaRepository()

        addUseCase = AddParkingAreaUseCaseImpl(fakeParkingRepo)
        updateUseCase = UpdateParkingAreaUseCaseImpl(fakeParkingRepo)
        getUserIdUseCase = FakeGetUserIdUseCase()
        getParkingAreaByIdUseCase = FakeGetParkingAreaByIdUseCase()
        getParkingAreaByIdUseCase.response = TestData.testParking


        viewModel = UpsertParkingAreaViewModel(
            addUseCase,
            updateUseCase,
            getUserIdUseCase,
            getParkingAreaByIdUseCase
        )

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun fillValidParkingData() {
        viewModel.onNameChange("Parking Integrado")
        viewModel.onAddressChange("Calle Falsa 123")
        viewModel.onCapacityChange(15)
        viewModel.onLocationChange(40.0, -3.0)
        viewModel.onOpeningTimeChange("09:00")
        viewModel.onClosingTimeChange("20:00")
    }

    @Test
    fun `Guardar parking nuevo con exito usando fakes`() = runTest(testDispatcher) {

        // Rellenamos el formulario a través del ViewModel
        fillValidParkingData()

        // Ejecutamos la lógica de guardado
        viewModel.onSaveParkingArea()
        testDispatcher.scheduler.advanceUntilIdle()

        // Asserts
        assertTrue(viewModel.state.value.isSuccess)
        assertNull(viewModel.state.value.error)

        val savedParking =
            fakeParkingRepo.getParkingAreasByOwner(getUserIdUseCase.response).getOrNull()?.first()
        assertNotNull(savedParking)
        assertEquals("Parking Integrado", savedParking.name)
        assertEquals(15, savedParking.capacity)

    }

    @Test
    fun `Error al guardar parking propaga el error hasta el ViewModel`() = runTest(testDispatcher) {
        // Forzamos al fake a fallar
        fakeParkingRepo.shouldReturnNetworkError = true

        fillValidParkingData()
        viewModel.onSaveParkingArea()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertFalse(viewModel.state.value.isSuccess)
        assertNotNull(viewModel.state.value.error)
    }

    @Test
    fun `Editar parking existente con exito`() = runTest(testDispatcher) {
        // Cargamos un parking en el estado para simular que estamos editando
        val parkingId = "park1"
        viewModel.loadParkingArea(parkingId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Modificamos el nombre
        viewModel.onNameChange("Nombre Editado")

        // Guardamos
        viewModel.onSaveParkingArea()
        testDispatcher.scheduler.advanceUntilIdle()

        // Verificamos que se llamó a la actualización
        assertTrue(viewModel.state.value.isSuccess)
    }

}