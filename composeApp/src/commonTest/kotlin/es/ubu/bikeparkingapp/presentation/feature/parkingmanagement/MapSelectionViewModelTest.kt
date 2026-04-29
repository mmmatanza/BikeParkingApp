package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement

import es.ubu.bikeparkingapp.domain.model.UserLocation
import es.ubu.bikeparkingapp.helper.usecases.location.FakeGetUserLocationUseCase
import es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.mapselection.MapSelectionViewModel
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

@OptIn(ExperimentalCoroutinesApi::class)
class MapSelectionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getUserLocationUseCase: FakeGetUserLocationUseCase
    private lateinit var viewModel: MapSelectionViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getUserLocationUseCase = FakeGetUserLocationUseCase()
        viewModel = MapSelectionViewModel(getUserLocationUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUserLocation carga coordenadas previas y luego la ubicacion real`() = runTest(testDispatcher) {
        // Preparación
        val prevLat = 10.0
        val prevLon = 10.0
        val realLocation = UserLocation(40.0, -3.0)
        getUserLocationUseCase.response = realLocation

        // Ejecución
        viewModel.loadUserLocation(prevLat, prevLon)

        // Assert inmediato
        assertEquals(prevLat, viewModel.state.value.latitude)

        // Esperamos a la corrutina
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert final
        val state = viewModel.state.value
        assertEquals(realLocation.latitude, state.userLatitude)
        assertEquals(prevLat, state.latitude, "La latitud seleccionada no debería cambiar por la ubicación real")
        assertFalse(state.isLoadingLocation)
    }

    @Test
    fun `onCoordinatesChange actualiza correctamente el estado`() {
        // Ejecución
        viewModel.onCoordinatesChange(42.0, -2.0)

        // Assert
        assertEquals(42.0, viewModel.state.value.latitude)
        assertEquals(-2.0, viewModel.state.value.longitude)
    }

    @Test
    fun `onClearCoordinates pone las coordenadas a null`() {
        // Preparación
        viewModel.onCoordinatesChange(1.0, 1.0)

        // Ejecución
        viewModel.onClearCoordinates()

        // Assert
        assertNull(viewModel.state.value.latitude)
        assertNull(viewModel.state.value.longitude)
    }

    @Test
    fun `onConfirmSelection devuelve las coordenadas actuales a traves del callback`() {
        // Preparación
        val lat = 37.0
        val lon = -5.0
        viewModel.onCoordinatesChange(lat, lon)

        var capturedLat: Double? = null
        var capturedLon: Double? = null

        // Ejecución
        viewModel.onConfirmSelection { resLat, resLon ->
            capturedLat = resLat
            capturedLon = resLon
        }

        // Assert
        assertEquals(lat, capturedLat)
        assertEquals(lon, capturedLon)
    }
}