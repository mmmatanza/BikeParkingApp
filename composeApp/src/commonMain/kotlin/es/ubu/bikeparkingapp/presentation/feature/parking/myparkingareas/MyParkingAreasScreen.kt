package es.ubu.bikeparkingapp.presentation.feature.parking.myparkingareas

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.accept
import bikeparkingapp.composeapp.generated.resources.error
import bikeparkingapp.composeapp.generated.resources.generic_error
import bikeparkingapp.composeapp.generated.resources.no_internet
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import es.ubu.bikeparkingapp.presentation.feature.parking.addparkingarea.AddParkingAreaScreen
import es.ubu.bikeparkingapp.presentation.feature.parking.parkingmanagement.ParkingManagementScreen
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

        MyParkingAreasContent(
            state,
            onBackClick = navigator::pop,
            onAddParkingAreaClick = {
                navigator.push(AddParkingAreaScreen())
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
                        else -> Text(stringResource(Res.string.generic_error))
                    }
                }
            )
        }
    }
}