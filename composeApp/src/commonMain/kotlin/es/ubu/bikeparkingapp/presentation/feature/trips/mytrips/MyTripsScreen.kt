package es.ubu.bikeparkingapp.presentation.feature.trips.mytrips

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.cancel_reservation
import bikeparkingapp.composeapp.generated.resources.cancel_reservation_confirm_user
import bikeparkingapp.composeapp.generated.resources.check_in
import bikeparkingapp.composeapp.generated.resources.check_in_reservation
import bikeparkingapp.composeapp.generated.resources.check_in_reservation_confirm_user
import bikeparkingapp.composeapp.generated.resources.check_out
import bikeparkingapp.composeapp.generated.resources.check_out_reservation
import bikeparkingapp.composeapp.generated.resources.check_out_reservation_confirm_user
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import es.ubu.bikeparkingapp.presentation.common.components.dialog.ConfirmationDialog
import es.ubu.bikeparkingapp.presentation.common.components.dialog.ErrorDialog
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Representa la pantalla de reservas.
 */
class MyTripsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<MyTripsViewModel>()
        val state = viewModel.state.value

        LaunchedEffect(Unit) {
            viewModel.loadTrips()
        }

        if (state.error != null)
            ErrorDialog(
                error = state.error,
                onDismiss = {
                    viewModel.clearState()
                    navigator.pop()
                }
            )

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        ConfirmationDialog(
            isVisible = state.checkInReservationDialog,
            title = stringResource(Res.string.check_in_reservation),
            message = stringResource(Res.string.check_in_reservation_confirm_user),
            onConfirm = viewModel::checkInReservation,
            onDismiss = viewModel::checkInReservationDialogDismiss
        )

        ConfirmationDialog(
            isVisible = state.checkOutReservationDialog,
            title = stringResource(Res.string.check_out_reservation),
            message = stringResource(Res.string.check_out_reservation_confirm_user),
            onConfirm = viewModel::checkOutReservation,
            onDismiss = viewModel::checkOutReservationDialogDismiss
        )

        
        ConfirmationDialog(
            isVisible = state.cancelReservationDialog,
            title = stringResource(Res.string.cancel_reservation),
            message = stringResource(Res.string.cancel_reservation_confirm_user),
            onConfirm = viewModel::cancelReservation,
            onDismiss = viewModel::cancelReservationDialogDismiss
        )

        MyTripsContent(
            state,
            actions = MyTripsActions(
                onBackClick = {
                    viewModel.clearState()
                    navigator.pop()
                },
                onCancelReservationClick = {
                    viewModel.cancelReservationDialog(it)
                },
                onCheckInClick = {
                    viewModel.checkInReservationDialog(it)
                },
                onCheckOutClick = {
                    viewModel.checkOutReservationDialog(it)
                }
            )
        )
    }
}