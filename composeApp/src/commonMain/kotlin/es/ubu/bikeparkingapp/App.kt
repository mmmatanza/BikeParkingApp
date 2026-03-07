package es.ubu.bikeparkingapp

import androidx.compose.runtime.Composable
import es.ubu.bikeparkingapp.presentation.navigation.AppNavigator
import es.ubu.bikeparkingapp.presentation.theme.AppTheme

/**
 * Representa la aplicación entera.
 */
@Composable
fun App() {
    AppTheme {
        AppNavigator()
    }
}