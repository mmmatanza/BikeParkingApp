package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.parkingmanagement

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.available_spots
import bikeparkingapp.composeapp.generated.resources.back
import bikeparkingapp.composeapp.generated.resources.closed
import bikeparkingapp.composeapp.generated.resources.deactivate
import bikeparkingapp.composeapp.generated.resources.edit_details
import bikeparkingapp.composeapp.generated.resources.error
import bikeparkingapp.composeapp.generated.resources.in_service
import bikeparkingapp.composeapp.generated.resources.out_of_service
import bikeparkingapp.composeapp.generated.resources.predicted_occupancy
import bikeparkingapp.composeapp.generated.resources.schedule
import bikeparkingapp.composeapp.generated.resources.status
import bikeparkingapp.composeapp.generated.resources.view_occupancy
import es.ubu.bikeparkingapp.domain.entity.isOpen
import es.ubu.bikeparkingapp.presentation.common.ext.handCursor
import org.jetbrains.compose.resources.stringResource

/**
 * Representa el contenido de la pantalla de detalle de un parking.
 * @param state Estado de la pantalla.
 * @param actions Acciones de la pantalla.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingManagementContent(
    state: ParkingManagementState,
    actions: ParkingManagementActions
) {
    val parking = state.parking

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = actions.onBackClick, modifier = Modifier.handCursor()) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                }
            )
        }
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Nombre del parking
                Text(
                    text = parking?.name.orEmpty(),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Plazas disponibles
                val occupancy = parking?.currentOccupancy ?: 0
                val capacity = parking?.capacity ?: 1
                val available = capacity - occupancy
                val progress = occupancy.toFloat() / capacity.toFloat()

                Text(
                    text = stringResource(Res.string.available_spots)
                            + "   $available / $capacity",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = when {
                        progress > 0.8f -> MaterialTheme.colorScheme.error
                        progress > 0.5f -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.primary
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Estado operativo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.status),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = when {
                            parking == null -> stringResource(Res.string.error)
                            !parking.isOperative -> stringResource(Res.string.out_of_service)
                            !parking.isOpen() -> stringResource(Res.string.closed)
                            else -> stringResource(Res.string.in_service)
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = when (parking?.isOperative) {
                            true -> MaterialTheme.colorScheme.primary
                            false -> MaterialTheme.colorScheme.error
                            null -> MaterialTheme.colorScheme.error
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Horarios
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.schedule),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "${parking?.openingTime?.take(5)} - ${parking?.closingTime?.take(5)}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // Predicción
                state.predictedOccupancy?.let { prediction ->
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(Res.string.predicted_occupancy),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        val capacity = parking?.capacity ?: 1
                        val available = capacity - prediction
                        Text(
                            text = "$available / $capacity",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Si el parking está en servicio, se muestra el botón de ver ocupación
                    if (parking?.isOperative == true) {
                        OutlinedButton(
                            onClick = {actions.onViewOccupancyClick(parking.parkingAreaId!!)},
                            modifier = Modifier.weight(1f).handCursor()
                        ) {
                            Text(stringResource(Res.string.view_occupancy))
                        }
                    }
                    OutlinedButton(
                        onClick = {actions.onEditDetailsClick(parking?.parkingAreaId!!)},
                        modifier = Modifier.weight(1f).handCursor()
                    ) {
                        Text(stringResource(Res.string.edit_details))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = actions.onToggleServiceClick,
                        modifier = Modifier.weight(1f).handCursor()
                    ) {
                        Text(
                            if (parking?.isOperative == true)
                                stringResource(Res.string.out_of_service)
                            else
                                stringResource(Res.string.in_service)
                        )
                    }
                    OutlinedButton(
                        onClick = actions.onDeactivateClick,
                        modifier = Modifier.weight(1f).handCursor(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Text(stringResource(Res.string.deactivate))
                    }
                }
            }
        }
    }
}

