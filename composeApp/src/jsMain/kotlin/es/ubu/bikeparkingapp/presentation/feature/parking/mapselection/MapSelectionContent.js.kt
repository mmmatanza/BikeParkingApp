package es.ubu.bikeparkingapp.presentation.feature.parking.mapselection


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.back
import bikeparkingapp.composeapp.generated.resources.select_location
import org.jetbrains.compose.resources.stringResource
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.style.rememberStyleState
import org.maplibre.compose.util.ClickResult
import org.maplibre.spatialk.geojson.Position

/**
 * Representa el contenido de la pantalla de selección de ubicación en el mapa.
 * @property state Estado actual de la pantalla.
 * @property onCoordinatesChange Función para manejar el cambio de coordenadas.
 * @property onLocationSelected Función para manejar la selección de ubicación.
 * @property onClearCoordinates Función para manejar el evento de limpiar coordenadas.
 * @property onBackClick Función para manejar el evento de retroceso.
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun MapSelectionContent(
    state: MapSelectionState,
    onCoordinatesChange: (Double, Double) -> Unit,
    onLocationSelected: () -> Unit,
    onClearCoordinates: () -> Unit,
    onBackClick: () -> Unit

) {
    val styleState = rememberStyleState()
    val cameraState = rememberCameraState(
        firstPosition = CameraPosition(
            target = Position(latitude = 40.4167, longitude = -3.7033),
            zoom = 15.0
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.select_location),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        // Cuando la ubicación del usuario se carga, actualiza la cámara
        LaunchedEffect(state.isLoadingLocation) {
            if (!state.isLoadingLocation) {
                cameraState.position = CameraPosition(
                    target = Position(
                        latitude = state.userLatitude,
                        longitude = state.userLongitude
                    ),
                    zoom = 15.0
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            MaplibreMap(
                onMapClick = { position, offset ->
                    onCoordinatesChange(position.latitude, position.longitude)
                    onLocationSelected()
                    ClickResult.Consume
                },
                modifier = Modifier.fillMaxSize(),
                cameraState = cameraState,
                styleState = styleState,
                baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty")
            )
        }
    }
}