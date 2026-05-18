package es.ubu.bikeparkingapp.presentation.feature.parkingexplorer.nearbyparkingareas

import es.ubu.bikeparkingapp.domain.entity.ParkingArea

/**
 * Representa el estado de la pantalla de áreas de parking cercanas.
 * @property isLoading Indica si se está cargando la información.
 * @property error Error ocurrido durante la carga de información.
 * @property recommendedArea Parking recomendado.
 * @property parkingAreas Lista de parkings disponibles (excluyendo el recomendado).
 * @property notAvailableParkingAreas Lista de parkings no disponibles.
 * @property userLatitude Latitud del usuario.
 * @property userLongitude Longitud del usuario.
 * @property isLoadingLocation Indica si se está cargando la ubicación del usuario.
 */
data class NearbyParkingAreasState(
    val isLoading: Boolean = false,
    val error: Exception? = null,
    val recommendedArea: ParkingArea? = null,
    val parkingAreas: List<ParkingArea> = emptyList(),
    val notAvailableParkingAreas: List<ParkingArea> = emptyList(),
    val userLatitude: Double = 0.0,
    val userLongitude: Double = 0.0,
    val isLoadingLocation: Boolean = true
)