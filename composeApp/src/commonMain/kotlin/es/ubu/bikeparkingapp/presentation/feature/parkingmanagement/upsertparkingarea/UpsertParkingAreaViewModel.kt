package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.upsertparkingarea

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.usecase.parking.AddParkingAreaUseCase
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingAreaByIdUseCase
import es.ubu.bikeparkingapp.domain.usecase.parking.UpdateParkingAreaUseCase
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserIdUseCase
import es.ubu.bikeparkingapp.presentation.common.util.ErrorMapper
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek

/**
 * Representa el viewmodel de la pantalla de agregar o modificar parking.
 * @property addParkingAreaUseCase Caso de uso para añadir un parking.
 * @property updateParkingAreaUseCase Caso de uso para actualizar un parking.
 * @property getUserIdUseCase Caso de uso para obtener el id del usuario.
 * @property getParkingAreaByIdUseCase Caso de uso para obtener un parking por su id.
 */
class UpsertParkingAreaViewModel(
    private val addParkingAreaUseCase: AddParkingAreaUseCase,
    private val updateParkingAreaUseCase: UpdateParkingAreaUseCase,
    private val getUserIdUseCase: GetUserIdUseCase,
    private val getParkingAreaByIdUseCase: GetParkingAreaByIdUseCase
) : ViewModel() {
    private val _state = mutableStateOf(UpsertParkingAreaState())
    val state: State<UpsertParkingAreaState> = _state

    fun loadParkingArea(parkingAreaId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            getParkingAreaByIdUseCase(parkingAreaId)
                .onSuccess { parking ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isAlreadyLoaded = true,
                        isEditing = true,
                        parkingAreaId = parking.parkingAreaId,
                        name = parking.name,
                        address = parking.address,
                        capacity = parking.capacity,
                        openingTime = parking.openingTime,
                        closingTime = parking.closingTime,
                        latitude = parking.latitude,
                        longitude = parking.longitude,
                        openDays = parking.openDays,
                        rules = parking.rules,
                        occupancyThreshold = parking.occupancyThreshold,
                        isOccupancyAlertEnabled = parking.occupancyThreshold != null
                    )
                    _state.value = _state.value.copy(isLoading = false)
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        error = ErrorMapper.map(it)
                    )
                }
        }
    }

    fun onSaveParkingArea() {
        viewModelScope.launch {
            val userId = getUserIdUseCase()
            if (userId.isFailure) {
                _state.value = _state.value.copy(error = userId.exceptionOrNull() as Exception?)
            } else {
                if (_state.value.isEditing){
                    updateParkingAreaUseCase(
                        parkingAreaId = _state.value.parkingAreaId!!,
                        ownerId = userId.getOrThrow(),
                        name = _state.value.name,
                        address = _state.value.address,
                        capacity = _state.value.capacity,
                        openingTime = _state.value.openingTime,
                        closingTime = _state.value.closingTime,
                        latitude = _state.value.latitude!!,
                        longitude = _state.value.longitude!!,
                        rules = _state.value.rules,
                        openDays = _state.value.openDays,
                        occupancyThreshold = if (_state.value.isOccupancyAlertEnabled) _state.value.occupancyThreshold else null
                    ).onFailure {
                        _state.value = _state.value.copy(
                            error = ErrorMapper.map(it)
                        )
                    }.onSuccess {
                        _state.value = _state.value.copy(isSuccess = true)
                    }
                }
                else {
                    addParkingAreaUseCase(
                        ownerId = userId.getOrThrow(),
                        name = _state.value.name,
                        address = _state.value.address,
                        capacity = _state.value.capacity,
                        openingTime = _state.value.openingTime,
                        closingTime = _state.value.closingTime,
                        latitude = _state.value.latitude!!,
                        longitude = _state.value.longitude!!,
                        rules = _state.value.rules,
                        openDays = _state.value.openDays,
                        occupancyThreshold = if (_state.value.isOccupancyAlertEnabled) _state.value.occupancyThreshold else null
                    ).onFailure {
                        _state.value = _state.value.copy(
                            error = ErrorMapper.map(it)
                        )
                    }.onSuccess {
                        _state.value = _state.value.copy(isSuccess = true)
                    }
                }
            }
        }
    }

    fun clearState(){
        _state.value = UpsertParkingAreaState()
    }

    fun clearError(){
        _state.value = _state.value.copy(error = null)
    }

    fun validateForm(): Boolean {
        val state = _state.value

        // Validaciones básicas previas
        val isBasicDataValid = state.name.isNotBlank() && state.address.isNotBlank() && state.capacity > 0

        // Validación de horario
        val isTimeValid = isClosingAfterOpening(state.openingTime, state.closingTime)

        // Tiene que haber coordenadas
        val areCoordinatesValid = (state.longitude != null) && (state.latitude != null)

        return isBasicDataValid && isTimeValid && areCoordinatesValid
    }

    /**
     * Comprueba si la hora de cierre es estrictamente posterior a la de apertura.
     * Formato esperado: "HH:mm"
     */
    private fun isClosingAfterOpening(opening: String, closing: String): Boolean {
        return try {
            val (openH, openM) = opening.split(":").map { it.toInt() }
            val (closeH, closeM) = closing.split(":").map { it.toInt() }

            val openInMinutes = openH * 60 + openM
            val closeInMinutes = closeH * 60 + closeM

            closeInMinutes > openInMinutes
        } catch (e: Exception) {
            false // Si el formato es incorrecto, no es válido
        }
    }

    fun toggleOpeningPicker(isActive:Boolean){
        _state.value = _state.value.copy(showOpeningPicker = isActive)
    }

    fun toggleClosingPicker(isActive:Boolean){
        _state.value = _state.value.copy(showClosingPicker = isActive)
    }

    fun onLocationChange(latitude: Double, longitude: Double) {
        _state.value = _state.value.copy(
            latitude = latitude,
            longitude = longitude
        )
    }

    fun onOpeningTimeChange(openingTime: String) {
        _state.value = _state.value.copy(openingTime = openingTime)
    }

    fun onClosingTimeChange(closingTime: String) {
        _state.value = _state.value.copy(closingTime = closingTime)
    }

    fun onDayToggle(day: DayOfWeek) {
        val current = _state.value.openDays
        _state.value = _state.value.copy(openDays = if (day in current) current - day else current + day)
    }

    fun onOpen24HoursToggle (enabled: Boolean){
        _state.value = _state.value.copy(
            isOpen24Hours = enabled,
            openingTime = if (enabled) "00:00" else _state.value.openingTime,
            closingTime = if (enabled) "23:59" else _state.value.closingTime,
        )
    }

    fun onCapacityChange(capacity: Int) {
        _state.value = _state.value.copy(capacity = capacity)
    }

    fun onNameChange(name: String) {
        _state.value = _state.value.copy(name = name)
    }

    fun onAddressChange(address: String) {
        _state.value = _state.value.copy(address = address)
    }

    fun onOccupancyThresholdChange(threshold: Int?) {
        _state.value = _state.value.copy(occupancyThreshold = threshold)
    }

    fun onOccupancyAlertToggle(enabled: Boolean) {
        _state.value = _state.value.copy(
            isOccupancyAlertEnabled = enabled,
            occupancyThreshold = if (enabled) _state.value.occupancyThreshold ?: 80 else null
        )
    }

    fun onRuleInputChange(value: String) {
        _state.value = _state.value.copy(currentRuleInput = value)
    }

    fun onAddRule() {
        val rule = _state.value.currentRuleInput.trim()
        if (rule.isBlank()) return
        _state.value = _state.value.copy(
            rules = _state.value.rules + rule,
            currentRuleInput = ""
        )
    }

    fun onRemoveRule(index: Int) {
        _state.value = _state.value.copy(
            rules = _state.value.rules.toMutableList().also { it.removeAt(index) }
        )
    }

    fun showUpsertConfirmationDialog(){
        _state.value = _state.value.copy(upsertConfirmationDialog = true)
    }

    fun upsertConfirmationDialogDismiss(){
        _state.value = _state.value.copy(upsertConfirmationDialog = false)
    }
}