package es.ubu.bikeparkingapp.presentation.feature.main

/**
 * Representa las acciones de la pantalla principal.
 */
data class MainActions (
    val onLogout: () -> Unit = {},
    val onNavigateToNearbyParkingAreas: () -> Unit = {},
    val onMyParkingAreas: () -> Unit = {},
    val onNavigateToMyTrips: () -> Unit = {}
)