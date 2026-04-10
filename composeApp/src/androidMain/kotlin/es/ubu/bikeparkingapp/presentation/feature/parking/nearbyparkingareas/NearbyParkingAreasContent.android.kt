package es.ubu.bikeparkingapp.presentation.feature.parking.nearbyparkingareas

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.back
import bikeparkingapp.composeapp.generated.resources.find_parking
import es.ubu.bikeparkingapp.domain.entity.ParkingArea
import org.jetbrains.compose.resources.stringResource
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.style.rememberStyleState
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position
import org.maplibre.spatialk.geojson.toJson
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.util.ClickResult

@OptIn(markerClass = [ExperimentalMaterial3Api::class])
@Composable
actual fun NearbyParkingAreasContent(
    state: NearbyParkingAreasState,
    onBackClick: () -> Unit,
    onParkingAreaClick: (String) -> Unit
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
                        stringResource(Res.string.find_parking),
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MaplibreMap(
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

                // Círculo tipo GPS para la posición del usuario
                CircleLayer(
                    id = "user-location-layer",
                    source = userLocationSource,
                    radius = const(8.dp),
                    color = const(Color.Blue),
                    strokeWidth = const(3.dp),
                    strokeColor = const(Color.Black)
                )

                // Source para los parking disponibles
                val availableSource = rememberGeoJsonSource(
                    data = if (state.parkingAreas.isNotEmpty()) {
                        GeoJsonData.JsonString(createParkingJson(state.parkingAreas))
                    } else {
                        GeoJsonData.JsonString("""{"type":"FeatureCollection","features":[]}""")
                    }
                )

                // Círculo para los parking disponibles
                CircleLayer(
                    id = "parkings-available-layer",
                    source = availableSource,
                    radius = const(8.dp),
                    color = const(Color.Green),
                    strokeWidth = const(3.dp),
                    strokeColor = const(Color.Black),
                    onClick = { features ->
                        // Obtenemos la propiedad "parkingId" que metimos en el JSON
                        val parkingId =
                            features[0].properties?.get("parkingId")?.toString()?.replace("\"", "")
                        if (parkingId != null) {
                            onParkingAreaClick(parkingId)
                        }
                        ClickResult.Consume
                    }
                )

                // Source para los parking no disponibles
                val notAvailableSource = rememberGeoJsonSource(
                    data = if (state.notAvailableParkingAreas.isNotEmpty()) {
                        GeoJsonData.JsonString(createParkingJson(state.notAvailableParkingAreas))
                    } else {
                        GeoJsonData.JsonString("""{"type":"FeatureCollection","features":[]}""")
                    }
                )

                // Círculo para los parking no disponibles
                CircleLayer(
                    id = "parkings-not-available-layer",
                    source = notAvailableSource,
                    radius = const(8.dp),
                    color = const(Color.Red),
                    strokeWidth = const(3.dp),
                    strokeColor = const(Color.Black)
                )
            }
        }
    }
}

fun createParkingJson(parkings: List<ParkingArea>): String {
    val features = parkings.joinToString(",") { parking ->
        """{"type":"Feature","geometry":{"type":"Point","coordinates":[${parking.longitude},${parking.latitude}]},"properties":{
                "parkingId": "${parking.id}" 
            }}"""
    }
    return """{"type":"FeatureCollection","features":[$features]}"""
}