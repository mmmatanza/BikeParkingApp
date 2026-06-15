package es.ubu.bikeparkingapp.presentation.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import es.ubu.bikeparkingapp.domain.model.AuthState
import es.ubu.bikeparkingapp.presentation.common.components.dialog.ErrorDialog
import es.ubu.bikeparkingapp.presentation.feature.alerts.AlertsScreen
import es.ubu.bikeparkingapp.presentation.feature.auth.login.LoginScreen
import es.ubu.bikeparkingapp.presentation.feature.myimpact.UserEcoDashboardScreen
import es.ubu.bikeparkingapp.presentation.feature.parkingexplorer.nearbyparkingareas.NearbyParkingAreasScreen
import es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.myparkingareas.MyParkingAreasScreen
import es.ubu.bikeparkingapp.presentation.feature.theme.ThemesMarketplaceScreen
import es.ubu.bikeparkingapp.presentation.feature.trips.mytrips.MyTripsScreen
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
            ErrorDialog(state.error) {
                viewModel.clearError()
            }
        }
        MainContent(
            state = state,
            authState = viewModel.authState.collectAsState(AuthState.Loading).value,
            actions = MainActions(
                onMyParkingAreas = {navigator.push(MyParkingAreasScreen())},
                onLogout = viewModel::onSignoutClick,
                onNavigateToNearbyParkingAreas = { navigator.push(NearbyParkingAreasScreen()) },
                onNavigateToMyTrips = { navigator.push(MyTripsScreen()) },
                onNavigateToAlerts = { navigator.push(AlertsScreen()) },
                onNavigateToMarketplace = { navigator.push(ThemesMarketplaceScreen()) },
                onNavigateToImpact = { navigator.push(UserEcoDashboardScreen()) }
            )
        )
    }
}
