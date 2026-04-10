package es.ubu.bikeparkingapp.presentation.feature.parking.parkingreservation

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

        LaunchedEffect(parkingAreaId){
            viewModel.loadParkingArea(parkingAreaId)
        }

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        ParkingReservationContent(
            state,
            onBackClick = { navigator.pop() },
            onConfirmClick = {}
        )
    }
}