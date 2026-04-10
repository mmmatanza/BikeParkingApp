package es.ubu.bikeparkingapp.presentation.feature.parking.nearbyparkingareas

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable

/**
 * Representa el contenido de la pantalla de áreas de parking cercanas.
 * @property state Estado actual de la pantalla.
 * @property onBackClick Función para manejar el evento de retroceso.
 * @property onParkingAreaClick Función para manejar el evento de selección de un parking.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
expect fun NearbyParkingAreasContent(
    state: NearbyParkingAreasState,
    onBackClick: () -> Unit,
    onParkingAreaClick: (String) -> Unit
)