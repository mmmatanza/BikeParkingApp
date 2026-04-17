package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.myparkingareas

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
import es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.parkingmanagement.ParkingManagementScreen
import es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.upsertparkingarea.UpsertParkingAreaScreen
import org.koin.compose.viewmodel.koinViewModel

/**
 * Representa la pantalla de lista de parkings.
 */
class MyParkingAreasScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<MyParkingAreasViewModel>()
        val state = viewModel.state.value

        LaunchedEffect(Unit) {
            viewModel.loadParkingAreas()
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

        MyParkingAreasContent(
            state,
            onBackClick = navigator::pop,
            onAddParkingAreaClick = {
                navigator.push(UpsertParkingAreaScreen())
            },
            onParkingAreaTouch = { parkingAreaId ->
                navigator.push(
                    ParkingManagementScreen(
                        parkingAreaId
                    )
                )
            },
            onSearchQueryChange = viewModel::onSearchQueryChange
        )
    }
}