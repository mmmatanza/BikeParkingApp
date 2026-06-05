package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.upsertparkingarea

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.accept
import bikeparkingapp.composeapp.generated.resources.cancel
import bikeparkingapp.composeapp.generated.resources.save_parking_area
import bikeparkingapp.composeapp.generated.resources.save_parking_area_confirmation
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import es.ubu.bikeparkingapp.presentation.common.components.dialog.ConfirmationDialog
import es.ubu.bikeparkingapp.presentation.common.components.dialog.ErrorDialog
import es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.mapselection.MapSelectionScreen
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Representa la pantalla de agregar o modificar parking.
 * @property parkingAreaIdToEdit Id del parking a editar.
 */
class UpsertParkingAreaScreen(
    private val parkingAreaIdToEdit: String? = null
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<UpsertParkingAreaViewModel>()
        val state = viewModel.state.value

        LaunchedEffect(parkingAreaIdToEdit) {
            parkingAreaIdToEdit?.let {
                if (!state.isAlreadyLoaded) viewModel.loadParkingArea(it)
            }
        }

        if (state.isSuccess) {
            viewModel.clearState()
            navigator.pop()
        }

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        if (state.error != null) {
            ErrorDialog(state.error) {
                viewModel.clearError()
            }
        }

        // Estado del reloj
        val timePickerState = rememberTimePickerState(
            initialHour = 8,
            initialMinute = 0,
            is24Hour = true
        )

        // Lógica del Diálogo
        if (state.showOpeningPicker || state.showClosingPicker) {
            AlertDialog(
                onDismissRequest = {
                    viewModel.toggleOpeningPicker(false)
                    viewModel.toggleClosingPicker(false)
                },
                confirmButton = {
                    TextButton(onClick = {
                        val formattedTime = "${
                            timePickerState.hour.toString().padStart(2, '0')
                        }:${timePickerState.minute.toString().padStart(2, '0')}"
                        if (state.showOpeningPicker) viewModel.onOpeningTimeChange(formattedTime)
                        else viewModel.onClosingTimeChange(formattedTime)

                        viewModel.toggleOpeningPicker(false)
                        viewModel.toggleClosingPicker(false)
                    }) { Text(stringResource(Res.string.accept)) }
                },
                dismissButton = {
                    TextButton(onClick = {
                        viewModel.toggleOpeningPicker(false)
                        viewModel.toggleClosingPicker(false)
                    }) { Text(stringResource(Res.string.cancel)) }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        TimePicker(state = timePickerState)
                    }
                }
            )
        }

        // Diálogo de confirmación
        ConfirmationDialog(
            state.upsertConfirmationDialog,
            onDismiss = viewModel::upsertConfirmationDialogDismiss,
            onConfirm = viewModel::onSaveParkingArea,
            title = stringResource(Res.string.save_parking_area),
            message = stringResource(Res.string.save_parking_area_confirmation)
        )

        UpsertParkingAreaContent(
            state,
            UpsertParkingAreaActions(
                onNameChange = viewModel::onNameChange,
                onAddressChange = viewModel::onAddressChange,
                onCapacityChange = viewModel::onCapacityChange,
                onOpeningTimeChange = viewModel::onOpeningTimeChange,
                onClosingTimeChange = viewModel::onClosingTimeChange,
                onBackClick = {
                    viewModel.clearState()
                    navigator.pop()
                },
                onSaveParkingArea = viewModel::showUpsertConfirmationDialog,
                onNavigateToMap = {
                    navigator.push(
                        MapSelectionScreen(
                            onLocationSelected = { lat, lng ->
                                // Si se ha seleccionado una ubicación se actualiza
                                if (lat != null && lng != null)
                                    viewModel.onLocationChange(lat, lng)
                            },
                            previousLatitude = state.latitude,
                            previousLongitude = state.longitude
                        )
                    )
                },
                validateForm = viewModel::validateForm,
                onRuleInputChange = viewModel::onRuleInputChange,
                onAddRule = viewModel::onAddRule,
                onRemoveRule = viewModel::onRemoveRule,
                onDayToggle = viewModel::onDayToggle,
                toggleOpeningPicker = viewModel::toggleOpeningPicker,
                toggleClosingPicker = viewModel::toggleClosingPicker,
                onOpen24HoursToggle = viewModel::onOpen24HoursToggle,
                onOccupancyThresholdChange = viewModel::onOccupancyThresholdChange,
                onOccupancyAlertToggle = viewModel::onOccupancyAlertToggle
            )
        )
    }
}