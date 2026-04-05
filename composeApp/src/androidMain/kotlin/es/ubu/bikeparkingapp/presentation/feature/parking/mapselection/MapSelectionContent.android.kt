package es.ubu.bikeparkingapp.presentation.feature.parking.mapselection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.accept
import bikeparkingapp.composeapp.generated.resources.back
import bikeparkingapp.composeapp.generated.resources.clear_coords
import bikeparkingapp.composeapp.generated.resources.select_location
import org.jetbrains.compose.resources.stringResource
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.style.rememberStyleState
import org.maplibre.compose.util.ClickResult
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position
import org.maplibre.spatialk.geojson.toJson

/**
 * Representa el contenido de la pantalla de selección de ubicación en el mapa.
 * @property state Estado actual de la pantalla.
 * @property onCoordinatesChange Función para manejar el cambio de coordenadas.
 * @property onLocationSelected Función para manejar la selección de ubicación.
 * @property onClearCoordinates Función para manejar el evento de limpiar coordenadas.
 * @property onBackClick Función para manejar el evento de retroceso.
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

    val userPosition = Position(
        latitude = state.userLatitude,
        longitude = state.userLongitude
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

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            MaplibreMap(
                onMapClick = { position, offset ->
                    onCoordinatesChange(position.latitude, position.longitude)
                    ClickResult.Consume
                },
                modifier = Modifier.fillMaxSize(),
                cameraState = cameraState,
                styleState = styleState,
                baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty")
            ) {
                // Solo pintamos la posición del usuario si tenemos coordenadas cargadas
                val userLocationSource = rememberGeoJsonSource(
                    data = if (!state.isLoadingLocation) {
                        GeoJsonData.JsonString(Point(userPosition).toJson())
                    } else {
                        GeoJsonData.JsonString("""{"type":"FeatureCollection","features":[]}""")
                    }
                )

                // Círculo tipo GPS
                CircleLayer(
                    id = "user-location-layer",
                    source = userLocationSource,
                    radius = const(8.dp),
                    color = const(Color.Green),
                    strokeWidth = const(3.dp),
                    strokeColor = const(Color.Black)
                )

                val markerSource = rememberGeoJsonSource(
                    data = if (state.latitude != null && state.longitude != null) {
                        GeoJsonData.JsonString(
                            Point(
                                Position(
                                    latitude = state.latitude,
                                    longitude = state.longitude
                                )
                            ).toJson()
                        )
                    } else {
                        GeoJsonData.JsonString("""{"type":"FeatureCollection","features":[]}""")
                    }
                )
                CircleLayer(
                    id = "marker-layer",
                    source = markerSource,
                    radius = const(10.dp),
                    color = const(Color.Red),
                    strokeWidth = const(3.dp),
                    strokeColor = const(Color.White)
                )
            }

            Surface(
                color = Color.Transparent,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = onLocationSelected,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(bottom = 16.dp)
                    ) {
                        Text(stringResource(Res.string.accept))
                    }
                    Button(
                        onClick = onClearCoordinates,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text(stringResource(Res.string.clear_coords))
                    }
                }
            }
        }
    }
}