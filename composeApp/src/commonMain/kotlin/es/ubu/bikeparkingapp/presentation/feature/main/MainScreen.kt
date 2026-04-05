package es.ubu.bikeparkingapp.presentation.feature.main

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.accept
import bikeparkingapp.composeapp.generated.resources.error
import bikeparkingapp.composeapp.generated.resources.generic_error
import bikeparkingapp.composeapp.generated.resources.no_internet
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import es.ubu.bikeparkingapp.domain.model.AuthState
import es.ubu.bikeparkingapp.presentation.feature.auth.login.LoginScreen
import es.ubu.bikeparkingapp.presentation.feature.parking.myparkingareas.MyParkingAreasScreen
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Representa la pantalla principal de la aplicación.
 */
class MainScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<MainViewModel>()
        val state = viewModel.state.value

        // Se observa el estado de autenticación del ViewModel
        LaunchedEffect(Unit) {
            viewModel.authState.collect { auth ->
                if (auth == AuthState.Unauthenticated)
                    navigator.replaceAll(LoginScreen())
            }
        }

        // Si cambia el estado de error, se muestra un diálogo con el mensaje de error
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
        MainContent(
            state = viewModel.state.value,
            authState = viewModel.authState.collectAsState(AuthState.Loading).value,
            onMyParkingAreas = {navigator.push(MyParkingAreasScreen())},
            onLogout = viewModel::onSignoutClick
        )
    }
}
