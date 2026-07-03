package es.ubu.bikeparkingapp.presentation.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import es.ubu.bikeparkingapp.presentation.feature.auth.login.LoginScreen

/**
 * Representa el navegador de la aplicación.
 * @param initialScreen Pantalla inicial del navegador.
 */
@Composable
fun AppNavigator(initialScreen: Screen = LoginScreen()){
    Navigator(initialScreen)
}