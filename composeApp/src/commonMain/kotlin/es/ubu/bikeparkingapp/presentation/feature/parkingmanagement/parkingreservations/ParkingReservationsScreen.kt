package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.parkingreservations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.cancel_reservation
import bikeparkingapp.composeapp.generated.resources.cancel_reservation_confirm
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import es.ubu.bikeparkingapp.presentation.common.components.dialog.ConfirmationDialog
import es.ubu.bikeparkingapp.presentation.common.components.dialog.ErrorDialog
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Representa la pantalla de reservas de un parking.
 * @property parkingAreaId Id del parking.
 */
class ParkingReservationsScreen(
    val parkingAreaId: String
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<ParkingReservationsViewModel>()
        val state = viewModel.state.value

        LaunchedEffect(Unit) {
            viewModel.loadReservations(parkingAreaId)
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

        ConfirmationDialog(
            isVisible = state.showCancelReservationDialog,
            title = stringResource(Res.string.cancel_reservation),
            message = stringResource(Res.string.cancel_reservation_confirm),
            onConfirm = viewModel::cancelReservation,
            onDismiss = viewModel::dismissCancelReservationDialog
        )

        ParkingReservationsContent(
            state,
            ParkingReservationsActions(
                onBackClick = {
                    navigator.pop()
                    viewModel.clearState()
                },
                onCancelReservationClick = {
                    viewModel.showCancelReservationDialog(it)
                }
            )
        )
    }
}