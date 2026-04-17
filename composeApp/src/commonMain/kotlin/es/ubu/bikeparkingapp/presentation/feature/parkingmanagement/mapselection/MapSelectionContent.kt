package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.mapselection

import androidx.compose.runtime.Composable

/**
 * Representa el contenido de la pantalla de selección de ubicación en el mapa.
 * @property state Estado actual de la pantalla.
 * @property onCoordinatesChange Función para manejar el cambio de coordenadas.
 * @property onLocationSelected Función para manejar la selección de ubicación.
 * @property onClearCoordinates Función para manejar el evento de limpiar coordenadas.
 * @property onBackClick Función para manejar el evento de retroceso.
 */
@Composable
expect fun MapSelectionContent(
    state: MapSelectionState,
    onCoordinatesChange: (Double, Double) -> Unit,
    onLocationSelected: () -> Unit,
    onClearCoordinates: () -> Unit,
    onBackClick: () -> Unit
)