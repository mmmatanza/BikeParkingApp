package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.myparkingareas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.back
import bikeparkingapp.composeapp.generated.resources.clear_search
import bikeparkingapp.composeapp.generated.resources.find_parking
import bikeparkingapp.composeapp.generated.resources.my_parking_areas_section
import bikeparkingapp.composeapp.generated.resources.no_parking_areas
import bikeparkingapp.composeapp.generated.resources.no_results_for
import es.ubu.bikeparkingapp.presentation.common.ext.handCursor
import org.jetbrains.compose.resources.stringResource

/**
 * Representa el contenido de la pantalla de lista de parkings.
 * @property state Estado actual de la pantalla.
 * @property onAddParkingAreaClick Función para manejar el evento de agregar parking.
 * @property onBackClick Función para manejar el evento de retroceso.
 * @property onParkingAreaTouch Función para manejar el toque en un parking.
 * @property onSearchQueryChange Función para manejar el cambio en la consulta de búsqueda.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyParkingAreasContent(
    state: MyParkingAreasState,
    onAddParkingAreaClick: () -> Unit,
    onBackClick: () -> Unit,
    onParkingAreaTouch: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit
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
                    IconButton(onClick = onBackClick, modifier = Modifier.handCursor()) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = { Text(stringResource(Res.string.find_parking)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = stringResource(Res.string.clear_search))
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.filteredParkingAreas?.let { list ->
                    items(list) { parkingArea ->
                        OutlinedCard(
                            onClick = { onParkingAreaTouch(parkingArea.parkingAreaId!!) },
                            modifier = Modifier.fillMaxWidth().handCursor()
                        ) {
                            Text(
                                text = parkingArea.name,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                if (state.filteredParkingAreas.isNullOrEmpty()) {
                    item {
                        Text(
                            text = if (state.searchQuery.isNotEmpty()) stringResource(Res.string.no_results_for) + state.searchQuery
                            else stringResource(Res.string.no_parking_areas),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}