package es.ubu.bikeparkingapp.presentation.feature.main

/**
 * Representa las acciones de la pantalla principal.
 * @param onLogout Función a ejecutar al hacer clic en el botón de logout.
 * @param onNavigateToNearbyParkingAreas Función a ejecutar al hacer clic en el botón de ver parqueos cercanos.
 * @param onMyParkingAreas Función a ejecutar al hacer clic en el botón de ver mis parkings.
 * @param onNavigateToMyTrips Función a ejecutar al hacer clic en el botón de ver mis viajes.
 * @param onNavigateToAlerts Función a ejecutar al hacer clic en el botón de ver alertas.
 * @param onNavigateToMarketplace Función a ejecutar al hacer clic en el botón de ver la tienda de temas.
 */
data class MainActions (
    val onLogout: () -> Unit = {},
    val onNavigateToNearbyParkingAreas: () -> Unit = {},
    val onMyParkingAreas: () -> Unit = {},
    val onNavigateToMyTrips: () -> Unit = {},
    val onNavigateToAlerts: () -> Unit = {},
    val onNavigateToMarketplace: () -> Unit = {}
)