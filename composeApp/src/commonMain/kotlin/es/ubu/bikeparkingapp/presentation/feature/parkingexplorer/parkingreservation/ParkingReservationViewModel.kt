package es.ubu.bikeparkingapp.presentation.feature.parkingexplorer.parkingreservation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.usecase.location.GetUserLocationUseCase
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingAreaByIdUseCase
import es.ubu.bikeparkingapp.domain.usecase.reservation.AddReservationUseCase
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserIdUseCase
import es.ubu.bikeparkingapp.presentation.common.util.ErrorMapper
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Representa el viewModel para la pantalla de reserva de plaza.
 * @property getParkingAreaByIdUseCase Caso de uso para obtener un parking por su id.
 * @property addReservationUseCase Caso de uso para añadir una reserva.
 * @property getUserIdUseCase Caso de uso para obtener el id del usuario actual.
 * @property getUserLocationUseCase Caso de uso para obtener la ubicación del usuario.
 */
class ParkingReservationViewModel(
    private val getParkingAreaByIdUseCase: GetParkingAreaByIdUseCase,
    private val addReservationUseCase: AddReservationUseCase,
    private val getUserIdUseCase: GetUserIdUseCase,
    private val getUserLocationUseCase: GetUserLocationUseCase
) : ViewModel() {
    private val _state = mutableStateOf(ParkingReservationState())
    val state: State<ParkingReservationState> = _state

    fun loadParkingArea(parkingAreaId: String) {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            getParkingAreaByIdUseCase(parkingAreaId)
                .onSuccess { parkingArea ->
                    _state.value = _state.value.copy(
                        parkingArea = parkingArea,
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

    fun availableParking(): Boolean {
        val area = _state.value.parkingArea ?: return false
        val hasSpace = (area.capacity - area.currentOccupancy) > 0
        return hasSpace && area.isOperative && area.isActive
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun clearState() {
        _state.value = ParkingReservationState()
    }

    fun confirmReservationDialog() {
        _state.value = _state.value.copy(confirmReservationDialog = true)
    }

    fun clearConfirmReservationDialog() {
        _state.value = _state.value.copy(confirmReservationDialog = false)
    }

    fun addReservation() {
        clearConfirmReservationDialog()
        val parkingArea = _state.value.parkingArea ?: return
        val parkingId = parkingArea.parkingAreaId ?: return
        viewModelScope.launch {
            val userLocation = getUserLocationUseCase().getOrNull()
            val distance = userLocation?.let {
                calculateDistance(
                    it.latitude, it.longitude,
                    parkingArea.latitude, parkingArea.longitude
                )
            }

            getUserIdUseCase().onSuccess { accountId ->
                addReservationUseCase(
                    parkingAreaId = parkingId,
                    accountId = accountId,
                    distance = distance
                ).onSuccess {
                    _state.value = _state.value.copy(successfulReservation = true)
                }.onFailure {
                    _state.value = _state.value.copy(
                        error = ErrorMapper.map(it)
                    )
                }
            }
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0 // Radio de la Tierra en metros
        val dLat = (lat2 - lat1).toRadians()
        val dLon = (lon2 - lon1).toRadians()
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1.toRadians()) * cos(lat2.toRadians()) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    private fun Double.toRadians(): Double = this * PI / 180.0
}