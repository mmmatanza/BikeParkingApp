package es.ubu.bikeparkingapp.presentation.feature.parkingexplorer.nearbyparkingareas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.back
import bikeparkingapp.composeapp.generated.resources.find_parking
import bikeparkingapp.composeapp.generated.resources.no_parking_found
import bikeparkingapp.composeapp.generated.resources.not_available_parking_areas
import es.ubu.bikeparkingapp.presentation.common.ext.handCursor
import org.jetbrains.compose.resources.stringResource

@OptIn(markerClass = [ExperimentalMaterial3Api::class])
@Composable
actual fun NearbyParkingAreasContent(
    state: NearbyParkingAreasState,
    onBackClick: () -> Unit,
    onParkingAreaClick: (String) -> Unit
) {
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
                    IconButton(onClick = onBackClick, modifier = Modifier.handCursor()) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.parkingAreas.isEmpty() && state.notAvailableParkingAreas.isEmpty() -> {
                    Text(
                        text = stringResource(Res.string.no_parking_found),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Sección de Parkings Disponibles
                        items(state.parkingAreas) { area ->
                            ParkingOptionItem(
                                name = area.name,
                                onClick = { onParkingAreaClick(area.parkingAreaId!!) }
                            )
                        }

                        // Sección de Parkings No Disponibles
                        if (state.notAvailableParkingAreas.isNotEmpty()) {
                            item {
                                Text(
                                    stringResource(Res.string.not_available_parking_areas),
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(state.notAvailableParkingAreas) { area ->
                                ParkingOptionItem(
                                    name = area.name,
                                    enabled = false,
                                    onClick = {}
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ParkingOptionItem(
    name: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().handCursor(),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) Color.Unspecified else Color.Gray
            )
            if (enabled) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        }
    }
}