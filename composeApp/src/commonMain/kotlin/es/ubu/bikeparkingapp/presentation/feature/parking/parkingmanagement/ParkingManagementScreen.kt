package es.ubu.bikeparkingapp.presentation.feature.parking.parkingmanagement

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
import bikeparkingapp.composeapp.generated.resources.no_internet
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import es.ubu.bikeparkingapp.presentation.feature.parking.addparkingarea.AddParkingAreaScreen
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Representa la pantalla de detalle de un parking.
 * @property parkingAreaId Id del parking.
 */
class ParkingManagementScreen(
    val parkingAreaId : String
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<ParkingManagementViewModel>()
        val state = viewModel.state.value

        LaunchedEffect(parkingAreaId) {
            parkingAreaId.let { viewModel.loadParkingArea(it) }
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
                        else -> state.error.message?.let { Text(it) }
                    }
                }
            )
        }

        // Si se desactiva el parking, se vuelve a la pantalla anterior
        if(state.successDeactivation) navigator.pop()

        ParkingManagementContent(
            state,
            actions = ParkingManagementActions(
                onBackClick = { navigator.pop() },
                onDeactivateClick = viewModel::onDeactivateClick,
                onDeactivateDialogDismiss = viewModel::onDeactivateDialogDismiss,
                onDeactivateConfirm = viewModel::onDeactivateConfirm,
                onToggleServiceClick = { viewModel.onToggleServiceClick() },
                onToggleServiceDismiss = viewModel::onToggleServiceDismiss,
                onToggleServiceConfirm = viewModel::onToggleConfirm,
                onViewOccupancyClick = {},
                onEditDetailsClick = {parkingId -> navigator.push(AddParkingAreaScreen(parkingId))}
            )
        )
    }
}