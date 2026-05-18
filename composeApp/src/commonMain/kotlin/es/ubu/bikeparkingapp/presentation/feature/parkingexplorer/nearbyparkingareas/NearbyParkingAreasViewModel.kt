package es.ubu.bikeparkingapp.presentation.feature.parkingexplorer.nearbyparkingareas

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.entity.isOpen
import es.ubu.bikeparkingapp.domain.usecase.location.GetUserLocationUseCase
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingDiscoveryUseCase
import es.ubu.bikeparkingapp.presentation.common.util.ErrorMapper
import kotlinx.coroutines.launch

/**
 * Representa el viewModel para la pantalla de áreas de parking cercanas.
 */
class NearbyParkingAreasViewModel(
    private val getParkingDiscoveryUseCase: GetParkingDiscoveryUseCase,
    private val getUserLocationUseCase: GetUserLocationUseCase
) : ViewModel() {
    private val _state = mutableStateOf(NearbyParkingAreasState())
    val state: State<NearbyParkingAreasState> = _state

    fun clearState() {
        _state.value = NearbyParkingAreasState()
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun loadUserLocation() {
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
                .onFailure {
                    _state.value = _state.value.copy(
                        error = ErrorMapper.map(it),
                        isLoadingLocation = false
                    )
                }
        }
    }

    private fun loadNearbyParkingAreas(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            getParkingDiscoveryUseCase(latitude, longitude, 5000.00)
                .onSuccess { discovery ->
                    // Separamos los parkings disponibles de los no disponibles de la lista allNearby
                    val (unavailable, available) = discovery.allNearby.partition { parking ->
                        parking.currentOccupancy >= parking.capacity || !parking.isOperative || !parking.isOpen()
                    }
                    _state.value = _state.value.copy(
                        recommendedArea = discovery.recommended,
                        parkingAreas = available,
                        notAvailableParkingAreas = unavailable,
                        isLoading = false
                    )
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        error = ErrorMapper.map(it),
                        isLoading = false
                    )
                }
        }
    }

}