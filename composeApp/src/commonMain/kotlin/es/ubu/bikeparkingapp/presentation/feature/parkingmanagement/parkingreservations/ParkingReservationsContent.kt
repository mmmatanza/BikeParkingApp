package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.parkingreservations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.back
import bikeparkingapp.composeapp.generated.resources.cancel
import bikeparkingapp.composeapp.generated.resources.canceled
import bikeparkingapp.composeapp.generated.resources.check_in
import bikeparkingapp.composeapp.generated.resources.check_out
import bikeparkingapp.composeapp.generated.resources.expired
import bikeparkingapp.composeapp.generated.resources.no_active_reservations
import bikeparkingapp.composeapp.generated.resources.overdue
import bikeparkingapp.composeapp.generated.resources.reservation_id
import bikeparkingapp.composeapp.generated.resources.reservations
import bikeparkingapp.composeapp.generated.resources.reserved
import bikeparkingapp.composeapp.generated.resources.since
import bikeparkingapp.composeapp.generated.resources.until
import bikeparkingapp.composeapp.generated.resources.user
import es.ubu.bikeparkingapp.domain.entity.Reservation
import es.ubu.bikeparkingapp.domain.entity.ReservationState
import es.ubu.bikeparkingapp.presentation.common.ext.handCursor
import es.ubu.bikeparkingapp.presentation.common.util.formatInstant
import org.jetbrains.compose.resources.stringResource

/**
 * Representa el contenido de la pantalla de reservas de un parking.
 * @param state Estado de la pantalla.
 * @param actions Acciones de la pantalla.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingReservationsContent(
    state: ParkingReservationsState,
    actions: ParkingReservationsActions
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.reservations)) },
                navigationIcon = {
                    IconButton(
                        onClick = actions.onBackClick, modifier = Modifier.handCursor()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        // Si el estado está cargando, mostramos un indicador
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.reservations.isEmpty()) {
            // Si no hay reservas
            Box(
                Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(Res.string.no_active_reservations), style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            // Lista de Reservas
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.reservations) { reservation ->
                    AdminReservationItem(
                        reservation = reservation,
                        onCancelClick = actions.onCancelReservationClick
                    )
                }
            }
        }
    }
}

@Composable
fun AdminReservationItem(
    reservation: Reservation,
    onCancelClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.user) + " ${reservation.accountId}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Estado de la reserva
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    // Mapeamos el estado de la reserva
                    val stateTextRes = when (reservation.state) {
                        ReservationState.RESERVED -> stringResource(Res.string.reserved)
                        ReservationState.CANCELLED -> stringResource(Res.string.canceled)
                        ReservationState.CHECKED_IN -> stringResource(Res.string.check_in)
                        ReservationState.CHECKED_OUT -> stringResource(Res.string.check_out)
                        ReservationState.OVERDUE -> stringResource(Res.string.overdue)
                        ReservationState.EXPIRED -> stringResource(Res.string.expired)
                    }
                    Text(
                        text = stateTextRes,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Información del tiempo
            Text(
                text = stringResource(Res.string.since) + ": ${formatInstant(reservation.inTime)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(Res.string.until) + ": ${formatInstant(reservation.outTime)}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.reservation_id) + ": ${reservation.reservationId}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
            if (reservation.state == ReservationState.RESERVED) {
                androidx.compose.material3.TextButton(
                    onClick = {onCancelClick(reservation.reservationId!!)},
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.handCursor()
                ) {
                    Text(
                        text = stringResource(Res.string.cancel),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
