package es.ubu.bikeparkingapp

import androidx.compose.runtime.Composable
import es.ubu.bikeparkingapp.di.initKoin
import es.ubu.bikeparkingapp.presentation.navigation.AppNavigator
import es.ubu.bikeparkingapp.presentation.theme.AppTheme

@Composable
fun App() {
    AppTheme {
        initKoin()
        AppNavigator()
    }
}