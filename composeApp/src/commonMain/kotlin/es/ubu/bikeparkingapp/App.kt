package es.ubu.bikeparkingapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import es.ubu.bikeparkingapp.domain.usecase.theme.GetAppliedThemeUseCase
import es.ubu.bikeparkingapp.presentation.feature.auth.login.LoginScreen
import es.ubu.bikeparkingapp.presentation.navigation.AppNavigator
import es.ubu.bikeparkingapp.presentation.theme.AppTheme
import org.koin.compose.koinInject

/**
 * Representa la aplicación entera.
 * @param initialScreen Pantalla inicial.
 */
@Composable
fun App(initialScreen: Screen = LoginScreen()) {
    val getAppliedThemeUseCase: GetAppliedThemeUseCase = koinInject()
    val appliedTheme by getAppliedThemeUseCase().collectAsState(initial = null)

    AppTheme(appliedTheme = appliedTheme) {
        AppNavigator(initialScreen = initialScreen)
    }
}
