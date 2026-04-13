package es.ubu.bikeparkingapp.presentation.feature.parking.parkingreservation

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
import bikeparkingapp.composeapp.generated.resources.account_has_active_reservation
import bikeparkingapp.composeapp.generated.resources.attention
import bikeparkingapp.composeapp.generated.resources.error
import bikeparkingapp.composeapp.generated.resources.no_internet
import bikeparkingapp.composeapp.generated.resources.reservation_advice
import bikeparkingapp.composeapp.generated.resources.reservation_created
import bikeparkingapp.composeapp.generated.resources.reset_advice
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import es.ubu.bikeparkingapp.domain.exception.AccountHasActiveReservationException
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Representa la pantalla de reserva de plaza
 * @property parkingAreaId Identificador del parking.
 */
class ParkingReservationScreen(
    private val parkingAreaId: String
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<ParkingReservationViewModel>()
        val state = viewModel.state.value

        LaunchedEffect(parkingAreaId) {
            viewModel.loadParkingArea(parkingAreaId)
        }

        if (state.successfulReservation) {
            AlertDialog(
                onDismissRequest = {
                    viewModel.clearState()
                    navigator.pop()
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.clearState()
                        navigator.pop()
                    }) {
                        Text(stringResource(Res.string.accept))
                    }
                },
                title = {
                    Text(stringResource(Res.string.attention))
                },
                text = {
                    Text(stringResource(Res.string.reservation_created))
                }
            )
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
                        is AccountHasActiveReservationException -> Text(stringResource(Res.string.account_has_active_reservation))
                        else -> Text(state.error.message!!)
                    }
                }
            )
        }

        if (state.confirmReservationDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.clearConfirmReservationDialog() },
                confirmButton = {
                    Button(onClick = {
                        viewModel.clearConfirmReservationDialog()
                        viewModel.addReservation()
                    }) {
                        Text(stringResource(Res.string.accept))
                    }
                },
                title = {
                    Text(stringResource(Res.string.attention))
                },
                text = {
                    Text(stringResource(Res.string.reservation_advice))
                }
            )
        }

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else
            ParkingReservationContent(
                state,
                onBackClick = { navigator.pop() },
                onConfirmClick = viewModel::confirmReservationDialog,
                availableParking = viewModel::availableParking
            )

    }
}