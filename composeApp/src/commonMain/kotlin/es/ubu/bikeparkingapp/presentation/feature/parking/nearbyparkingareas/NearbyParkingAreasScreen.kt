package es.ubu.bikeparkingapp.presentation.feature.parking.nearbyparkingareas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import es.ubu.bikeparkingapp.presentation.feature.parking.parkingreservation.ParkingReservationScreen
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Representa la pantalla de áreas de parking cercanas.
 */
class NearbyParkingAreasScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<NearbyParkingAreasViewModel>()
        val state = viewModel.state.value

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
                        //else -> Text(stringResource(Res.string.generic_error))
                        else -> Text(state.error.message!!)
                    }
                }
            )
        }

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        NearbyParkingAreasContent(
            state,
            onBackClick = { navigator.pop() },
            onParkingAreaClick = {
                navigator.push(ParkingReservationScreen(it))
            }
        )
    }
}