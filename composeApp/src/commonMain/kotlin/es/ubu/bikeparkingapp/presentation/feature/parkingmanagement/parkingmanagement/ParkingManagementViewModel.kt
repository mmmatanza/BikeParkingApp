package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.parkingmanagement

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.usecase.occupancy.GetPredictedOccupancyUseCase
import es.ubu.bikeparkingapp.domain.usecase.parking.DeactivateParkingAreaUseCase
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingAreaByIdUseCase
import es.ubu.bikeparkingapp.domain.usecase.parking.ToggleOperativeStateUseCase
import es.ubu.bikeparkingapp.presentation.common.util.ErrorMapper
import kotlinx.coroutines.launch

/**
 * Representa el viewmodel de la pantalla de detalle de un parking.
 * @property getParkingAreaByIdUseCase Caso de uso para obtener un parking.
 * @property deactivateParkingAreaUseCase Caso de uso para desactivar un parking.
 * @property toggleOperativeStateUseCase Caso de uso para cambiar el estado operativo de un parking.
 * @property getPredictedOccupancyUseCase Caso de uso para obtener la ocupación predicha.
 */
class ParkingManagementViewModel(
    private val getParkingAreaByIdUseCase: GetParkingAreaByIdUseCase,
    private val deactivateParkingAreaUseCase: DeactivateParkingAreaUseCase,
    private val toggleOperativeStateUseCase: ToggleOperativeStateUseCase,
    private val getPredictedOccupancyUseCase: GetPredictedOccupancyUseCase
) : ViewModel() {
    private val _state = mutableStateOf(ParkingManagementState())
    val state: State<ParkingManagementState> = _state

    fun clearError(){
        _state.value = _state.value.copy(error = null)
    }
    fun onDeactivateClick() {
        _state.value = _state.value.copy(showDeactivateDialog = true)
    }

    fun onDeactivateDialogDismiss() {
        _state.value = _state.value.copy(showDeactivateDialog = false)
    }

    fun onDeactivateConfirm() {
        _state.value = _state.value.copy(showDeactivateDialog = false)
        onDeactivateParking()
    }

    fun onToggleServiceClick() {
        _state.value = _state.value.copy(showToggleDialog = true)
    }

    fun onToggleServiceDismiss() {
        _state.value = _state.value.copy(showToggleDialog = false)
    }

    fun onToggleConfirm() {
        _state.value = _state.value.copy(showToggleDialog = false)
        if(state.value.parking !=null)
            onChangeServiceState(!state.value.parking!!.isOperative)
    }

    fun loadParkingArea(parkingAreaId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            getParkingAreaByIdUseCase(parkingAreaId)
                .onSuccess { parking ->
                    _state.value = _state.value.copy(
                        parking = parking,
                        isLoading = false
                    )
                    loadPrediction(parkingAreaId)
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        error = ErrorMapper.map(it),
                        isLoading = false
                    )
                }
        }
    }

    private fun loadPrediction(parkingAreaId: String) {
        viewModelScope.launch {
            getPredictedOccupancyUseCase(parkingAreaId)
                .onSuccess { prediction ->
                    _state.value = _state.value.copy(predictedOccupancy = prediction)
                }
                .onFailure {
                    it.printStackTrace()
                }
        }
    }

    fun onChangeServiceState(isOperative: Boolean){
        viewModelScope.launch {
            toggleOperativeStateUseCase(state.value.parking?.parkingAreaId ?: return@launch, isOperative)
                .onSuccess {
                    _state.value =
                        _state.value.copy(parking = _state.value.parking?.copy(isOperative = isOperative))
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        error = ErrorMapper.map(it)
                    )
                }
        }
    }

    fun onDeactivateParking(){
        viewModelScope.launch {
            val parkingId = state.value.parking?.parkingAreaId ?: return@launch
            deactivateParkingAreaUseCase(parkingId)
                .onSuccess {
                    _state.value = _state.value.copy(successDeactivation = true)
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        error = ErrorMapper.map(it)
                    )
                }
        }
    }
}