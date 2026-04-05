package es.ubu.bikeparkingapp.presentation.feature.parking.myparkingareas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.back
import bikeparkingapp.composeapp.generated.resources.my_parking_areas_section
import bikeparkingapp.composeapp.generated.resources.no_parking_areas
import org.jetbrains.compose.resources.stringResource

/**
 * Representa el contenido de la pantalla de lista de parkings.
 * @property state Estado actual de la pantalla.
 * @property onAddParkingAreaClick Función para manejar el evento de agregar parking.
 * @property onBackClick Función para manejar el evento de retroceso.
 * @property onParkingAreaTouch Función para manejar el toque en un parking.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyParkingAreasContent(
    state: MyParkingAreasState,
    onAddParkingAreaClick: () -> Unit,
    onBackClick: () -> Unit,
    onParkingAreaTouch: (String) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.my_parking_areas_section),
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddParkingAreaClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Text("+", style = MaterialTheme.typography.headlineSmall)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre opciones
            ) {
                // Si hay parkings, se listan como tarjetas clickeables
                state.parkingAreas?.let { list ->
                    items(list) { parkingArea ->
                        OutlinedCard(
                            onClick = {
                                onParkingAreaTouch(parkingArea.id!!)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = parkingArea.name,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                // Si la lista está vacía o es nula
                if (state.parkingAreas.isNullOrEmpty()) {
                    item {
                        Text(
                            text = stringResource(Res.string.no_parking_areas),
                            modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}