package es.ubu.bikeparkingapp.presentation.feature.parking.upsertparkingarea

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.accept
import bikeparkingapp.composeapp.generated.resources.error
import bikeparkingapp.composeapp.generated.resources.generic_error
import bikeparkingapp.composeapp.generated.resources.no_internet
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import es.ubu.bikeparkingapp.presentation.feature.parking.mapselection.MapSelectionScreen
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Representa la pantalla de agregar o modificar parking.
 * @property parkingAreaIdToEdit Id del parking a editar.
 */
class UpsertParkingAreaScreen(
    private val parkingAreaIdToEdit: String? = null
) : Screen {
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
            AlertDialog(
                onDismissRequest = { viewModel.clearError() },
                confirmButton = {
                    Button(onClick = { viewModel.clearError() }) {
                        Text(stringResource(Res.string.accept))
                    }
                },
                title = {
                    Text(stringResource(Res.string.error))
                },
                text = {
                    when (state.error) {
                        is NoNetworkException -> Text(stringResource(Res.string.no_internet))
                        else -> Text(stringResource(Res.string.generic_error))
                    }
                }
            )
        }

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
                onSaveParkingArea = viewModel::onSaveParkingArea,
                onNavigateToMap = {
                    navigator.push(MapSelectionScreen(onLocationSelected = { lat, lng ->
                        // Si se ha seleccionado una ubicación se actualiza
                        if (lat != null && lng != null)
                            viewModel.onLocationChange(lat, lng)
                    }))
                },
                validateForm = viewModel::validateForm,
                onRuleInputChange = viewModel::onRuleInputChange,
                onAddRule = viewModel::onAddRule,
                onRemoveRule = viewModel::onRemoveRule,
                onDayToggle = viewModel::onDayToggle
            )
        )
    }
}