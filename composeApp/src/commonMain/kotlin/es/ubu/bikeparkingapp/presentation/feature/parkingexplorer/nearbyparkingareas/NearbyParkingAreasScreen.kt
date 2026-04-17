package es.ubu.bikeparkingapp.presentation.feature.parkingexplorer.nearbyparkingareas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import es.ubu.bikeparkingapp.presentation.common.components.dialog.ErrorDialog
import es.ubu.bikeparkingapp.presentation.feature.parkingexplorer.parkingreservation.ParkingReservationScreen
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

        LaunchedEffect(Unit){
            viewModel.loadUserLocation()
        }

        if (state.error != null) {
            ErrorDialog(state.error) {
                viewModel.clearError()
            }
        }

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        NearbyParkingAreasContent(
            state,
            onBackClick = {
                navigator.pop()
                viewModel.clearState()
            },
            onParkingAreaClick = {
                navigator.push(ParkingReservationScreen(it))
            }
        )
    }
}