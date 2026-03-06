package es.ubu.bikeparkingapp.presentation.feature.main

data class MainState (
    val points:Int = 0,
    val error: String? = null,
    val isLoggedIn: Boolean = true
)