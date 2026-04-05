package es.ubu.bikeparkingapp.presentation.feature.parking.mapselection

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.usecase.location.GetUserLocationUseCase
import kotlinx.coroutines.launch

/**
 * Representa el viewmodel de la pantalla de selección de ubicación en el mapa.
 * @property getUserLocationUseCase Caso de uso para obtener la ubicación del usuario.
 */
class MapSelectionViewModel(
    private val getUserLocationUseCase: GetUserLocationUseCase
): ViewModel() {
    private val _state = mutableStateOf(MapSelectionState())
    val state: State<MapSelectionState> = _state

    init {
        loadUserLocation()
    }

    private fun loadUserLocation() {
        viewModelScope.launch {
            getUserLocationUseCase()
                .onSuccess { location ->
                    _state.value = _state.value.copy(
                        userLatitude = location.latitude,
                        userLongitude = location.longitude,
                        isLoadingLocation = false
                    )
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isLoadingLocation = false
                    )
                }
        }
    }

    fun onClearCoordinates(){
        _state.value = _state.value.copy(
            latitude = null,
            longitude = null
        )
    }

    fun onCoordinatesChange(latitude: Double, longitude: Double){
        _state.value = _state.value.copy(
            latitude = latitude,
            longitude = longitude
        )
    }

    fun onConfirmSelection(onSuccess: (Double?, Double?) -> Unit) {
        val lat = state.value.latitude
        val lon = state.value.longitude
        onSuccess(lat, lon)
    }

}