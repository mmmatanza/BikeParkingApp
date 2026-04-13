package es.ubu.bikeparkingapp.presentation.feature.parking.myparkingareas

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
import es.ubu.bikeparkingapp.presentation.feature.parking.parkingmanagement.ParkingManagementScreen
import es.ubu.bikeparkingapp.presentation.feature.parking.upsertparkingarea.UpsertParkingAreaScreen
import org.jetbrains.compose.resources.stringResource
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
            }
        )
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
                        else -> Text(state.error.message!!)
                    }
                }
            )
        }
    }
}