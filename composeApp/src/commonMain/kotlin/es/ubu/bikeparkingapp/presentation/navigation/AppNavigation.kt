package es.ubu.bikeparkingapp.presentation.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import es.ubu.bikeparkingapp.presentation.feature.login.LoginScreen

/**
 * Representa el navegador de la aplicación.
 */
@Composable
fun AppNavigator(){
    Navigator(LoginScreen())
}