package es.ubu.bikeparkingapp.presentation.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import es.ubu.bikeparkingapp.presentation.feature.login.LoginScreen

@Composable
fun AppNavigator(){
    Navigator(LoginScreen())
}