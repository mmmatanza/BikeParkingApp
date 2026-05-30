package es.ubu.bikeparkingapp.presentation.feature.parkingexplorer

import es.ubu.bikeparkingapp.domain.entity.ParkingDiscovery
import es.ubu.bikeparkingapp.domain.model.UserLocation
import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.usecases.location.FakeGetUserLocationUseCase
import es.ubu.bikeparkingapp.helper.usecases.parking.FakeGetParkingDiscoveryUseCase
import es.ubu.bikeparkingapp.presentation.feature.parkingexplorer.nearbyparkingareas.NearbyParkingAreasViewModel
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

@OptIn(ExperimentalCoroutinesApi::class)
class NearbyParkingAreasViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: NearbyParkingAreasViewModel
    private lateinit var getParkingDiscoveryUseCase: FakeGetParkingDiscoveryUseCase
    private lateinit var getUserLocationUseCase: FakeGetUserLocationUseCase

    @BeforeTest
    fun setUp() {
        // Obligatorio para viewModelScope
        Dispatchers.setMain(testDispatcher)
        getParkingDiscoveryUseCase = FakeGetParkingDiscoveryUseCase()
        getUserLocationUseCase = FakeGetUserLocationUseCase()

        viewModel = NearbyParkingAreasViewModel(
            getParkingDiscoveryUseCase,
            getUserLocationUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUserLocation actualiza coordenadas y separa parkings correctamente`() =
        runTest(testDispatcher) {
            // Preparación
            val lat = 40.0
            val lon = -3.0
            getUserLocationUseCase.response = UserLocation(lat, lon)

            val baseParking = TestData.testParking

            // El parking libre debe tener suficiente capacidad
            val pLibre = baseParking.copy(
                parkingAreaId = "libre",
                capacity = 10,
                currentOccupancy = 0
            )

            // El parking lleno debe cumplir la condición de ocupación >= capacidad
            val pLleno = baseParking.copy(
                parkingAreaId = "lleno",
                capacity = 10,
                currentOccupancy = 10
            )

            getParkingDiscoveryUseCase.response = ParkingDiscovery(
                recommended = null,
                allNearby = listOf(pLibre, pLleno)
            )

            // Ejecución
            viewModel.loadUserLocation()

            // Avanzamos
            testDispatcher.scheduler.advanceUntilIdle()

            // Asserts
            val state = viewModel.state.value

            assertEquals(lat, state.userLatitude, "La latitud no se actualizó en el estado")
            assertFalse(state.isLoadingLocation, "isLoadingLocation debería ser false")

            assertEquals(
                1,
                state.parkingAreas.size,
                "Debería haber 1 parking disponible (el libre)"
            )
            assertEquals("libre", state.parkingAreas[0].parkingAreaId)

            assertEquals(
                1,
                state.notAvailableParkingAreas.size,
                "Debería haber 1 parking no disponible (el lleno)"
            )
            assertEquals("lleno", state.notAvailableParkingAreas[0].parkingAreaId)

            assertFalse(state.isLoading, "isLoading final debería ser false")
        }
}