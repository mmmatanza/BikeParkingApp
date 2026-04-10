package es.ubu.bikeparkingapp.presentation.feature.parking.nearbyparkingareas

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import es.ubu.bikeparkingapp.domain.usecase.location.GetUserLocationUseCase
import es.ubu.bikeparkingapp.domain.usecase.parking.GetNearbyParkingAreasUseCase
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/**
 * Representa el viewModel para la pantalla de áreas de parking cercanas.
 */
class NearbyParkingAreasViewModel(
    private val getNearbyParkingAreasUseCase: GetNearbyParkingAreasUseCase,
    private val getUserLocationUseCase: GetUserLocationUseCase
) : ViewModel() {
    private val _state = mutableStateOf(NearbyParkingAreasState())
    val state: State<NearbyParkingAreasState> = _state

    init {
        loadUserLocation()
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    private fun loadUserLocation() {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            getUserLocationUseCase()
                .onSuccess { location ->
                    _state.value = _state.value.copy(
                        userLatitude = location.latitude,
                        userLongitude = location.longitude,
                        isLoadingLocation = false
                    )
                    loadNearbyParkingAreas(location.latitude, location.longitude)
                }
                .onFailure { error ->
                    when (error) {
                        is NoNetworkException -> _state.value = _state.value.copy(error = error)
                        else -> _state.value = _state.value.copy(error = Exception(error.message))
                    }
                    _state.value = _state.value.copy(
                        isLoadingLocation = false
                    )
                }
        }
    }

    private fun loadNearbyParkingAreas(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            getNearbyParkingAreasUseCase(latitude, longitude, 5000.00)
                .onSuccess { parkingAreas ->
                    val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).dayOfWeek
                    // Separamos los parkings disponibles de los no disponibles
                    val (unavailable, available) = parkingAreas.partition { parking ->
                        parking.currentOccupancy >= parking.capacity || !parking.isOperative ||
                                today !in parking.openDays
                    }
                    _state.value = _state.value.copy(
                        parkingAreas = available,
                        notAvailableParkingAreas = unavailable,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(isLoading = false)
                    when (error) {
                        is NoNetworkException -> _state.value = _state.value.copy(error = error)
                        else -> _state.value = _state.value.copy(error = Exception(error.message))
                    }
                }
        }
    }

}