package es.ubu.bikeparkingapp.presentation.feature.parking.mapselection

/**
 * Representa el estado de la pantalla de selección de ubicación en el mapa.
 * @property latitude Latitud seleccionada.
 * @property longitude Longitud seleccionada.
 * @property isLoadingLocation Indica si se está cargando la ubicación del usuario.
 * @property userLatitude Latitud del usuario.
 * @property userLongitude Longitud del usuario.
 * @property error Error ocurrido durante la selección.
 */
data class MapSelectionState(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isLoadingLocation: Boolean = true,
    val userLatitude: Double = 0.0,
    val userLongitude: Double = 0.0,
    val error: Exception? = null
)