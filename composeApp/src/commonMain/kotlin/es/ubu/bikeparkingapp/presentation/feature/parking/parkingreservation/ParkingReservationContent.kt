package es.ubu.bikeparkingapp.presentation.feature.parking.parkingreservation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.available_spots_out_of
import bikeparkingapp.composeapp.generated.resources.back
import bikeparkingapp.composeapp.generated.resources.confirm_reservation
import bikeparkingapp.composeapp.generated.resources.error
import bikeparkingapp.composeapp.generated.resources.no_rules
import bikeparkingapp.composeapp.generated.resources.parking_rules
import bikeparkingapp.composeapp.generated.resources.schedule
import org.jetbrains.compose.resources.stringResource

/**
 * Representa el contenido de la pantalla de reserva de plaza.
 * @property state Estado actual de la pantalla.
 * @property onBackClick Acciones que se pueden realizar en la pantalla.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingReservationContent(
    state: ParkingReservationState,
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit,
    availableParking: () -> Boolean
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { state.parkingArea?.name?.let { Text(it) } },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Nombre del parking
                state.parkingArea?.name?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 40.sp
                    )
                }

                // Card de disponibilidad
                state.parkingArea?.capacity?.let{
                    val availableSpots = state.parkingArea.capacity - state.parkingArea.currentOccupancy
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (availableSpots > 0)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = availableSpots.toString() + stringResource(Res.string.available_spots_out_of) + state.parkingArea.capacity,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Horario
                state.parkingArea?.closingTime?.let {
                    SectionInfo(
                        title = stringResource(Res.string.schedule),
                        icon = Icons.Default.DateRange
                    ) {
                        Text(
                            text = "${state.parkingArea.openingTime.take(5)} - ${state.parkingArea.closingTime.take(5)}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // Normas
                state.parkingArea?.rules?.let {
                    SectionInfo(
                        title = stringResource(Res.string.parking_rules),
                        icon = Icons.Default.Info
                    ) {
                        if (state.parkingArea.rules.isEmpty()) {
                            Text(
                                text = stringResource(Res.string.no_rules),
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = FontStyle.Italic
                            )
                        } else {
                            state.parkingArea.rules.forEach { rule ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text("• ", style = MaterialTheme.typography.bodyLarge)
                                    Text(
                                        text = rule,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Botón de Confirmación
                Button(
                    onClick = onConfirmClick,
                    enabled = availableParking(),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Text(
                        text = stringResource(Res.string.confirm_reservation),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
fun SectionInfo(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
        }
        content()
    }
}