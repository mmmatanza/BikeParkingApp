package es.ubu.bikeparkingapp.presentation.feature.trips.mytrips

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.Button
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.address
import bikeparkingapp.composeapp.generated.resources.back
import bikeparkingapp.composeapp.generated.resources.cancel
import bikeparkingapp.composeapp.generated.resources.canceled
import bikeparkingapp.composeapp.generated.resources.check_in
import bikeparkingapp.composeapp.generated.resources.check_out
import bikeparkingapp.composeapp.generated.resources.expired
import bikeparkingapp.composeapp.generated.resources.extend
import bikeparkingapp.composeapp.generated.resources.extend_reservation
import bikeparkingapp.composeapp.generated.resources.my_trips
import bikeparkingapp.composeapp.generated.resources.no_active_reservations
import bikeparkingapp.composeapp.generated.resources.overdue
import bikeparkingapp.composeapp.generated.resources.reserved
import bikeparkingapp.composeapp.generated.resources.since
import bikeparkingapp.composeapp.generated.resources.until
import es.ubu.bikeparkingapp.domain.entity.ReservationDetail
import es.ubu.bikeparkingapp.domain.entity.ReservationState
import es.ubu.bikeparkingapp.presentation.common.ext.handCursor
import es.ubu.bikeparkingapp.presentation.common.util.formatInstant
import org.jetbrains.compose.resources.stringResource

/**
 * Representa el contenido de la pantalla de reservas.
 * @param state Estado de la pantalla.
 * @param actions Acciones de la pantalla.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTripsContent(
    state: MyTripsState,
    actions: MyTripsActions
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.my_trips),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
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
                items(state.reservations) { reservationDetail ->
                    UserReservationItem(
                        reservationDetail = reservationDetail,
                        onCancelClick = actions.onCancelReservationClick,
                        onCheckInClick = actions.onCheckInClick,
                        onCheckOutClick = actions.onCheckOutClick,
                        onExtendClick = actions.onExtendReservationClick
                    )
                }
            }
        }
    }
}

/**
 * Representa un item de reserva en la pantalla de reservas del usuario
 * @param reservationDetail Detalles de la reserva.
 * @param onCancelClick Acción al hacer click en el botón de cancelar
 * @param onCheckInClick Acción al hacer click en el botón de check-in
 * @param onCheckOutClick Acción al hacer click en el botón de check-out
 * @param onExtendClick Acción al hacer click en el botón de extender
 */
@Composable
fun UserReservationItem(
    reservationDetail: ReservationDetail,
    onCancelClick: (String) -> Unit,
    onCheckInClick: (String) -> Unit,
    onCheckOutClick: (String) -> Unit,
    onExtendClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Cabecera: ID + Badge de estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "${reservationDetail.reservation.reservationId}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    softWrap = true // Permite que el ID salte de línea
                )

                ReservationStateBadge(reservationDetail.reservation.state)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Información de tiempo compacta
            FlowRow( // FlowRow por si no cabe en una línea
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "${stringResource(Res.string.since)}: ${formatInstant(reservationDetail.reservation.inTime)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${stringResource(Res.string.until)}: ${formatInstant(reservationDetail.reservation.outTime)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Información de la localización
            FlowRow( // FlowRow por si no cabe en una línea
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(Res.string.address) + ": ${reservationDetail.parkingAddress}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (reservationDetail.reservation.hasActions) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End, // Alinea los botones a la derecha
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (reservationDetail.reservation.canCancel) {
                        TextButton(
                            onClick = { onCancelClick(reservationDetail.reservation.reservationId!!) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = stringResource(Res.string.cancel),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { onCheckInClick(reservationDetail.reservation.reservationId!!) },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = stringResource(Res.string.check_in),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    if(reservationDetail.reservation.canExtend){
                        Button(
                            onClick = { onExtendClick(reservationDetail.reservation.reservationId!!) },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = stringResource(Res.string.extend),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    if (reservationDetail.reservation.canCheckOut) {
                        Button(
                            onClick = { onCheckOutClick(reservationDetail.reservation.reservationId!!) },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = stringResource(Res.string.check_out),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Representa el badge de estado de una reserva
 * @param state Estado de la reserva.
 */
@Composable
private fun ReservationStateBadge(state: ReservationState) {
    val (textRes, containerColor) = when (state) {
        ReservationState.RESERVED -> Res.string.reserved to MaterialTheme.colorScheme.secondaryContainer
        ReservationState.CANCELLED -> Res.string.canceled to MaterialTheme.colorScheme.errorContainer
        ReservationState.CHECKED_IN -> Res.string.check_in to MaterialTheme.colorScheme.tertiaryContainer
        ReservationState.CHECKED_OUT -> Res.string.check_out to MaterialTheme.colorScheme.surfaceVariant
        ReservationState.OVERDUE -> Res.string.overdue to MaterialTheme.colorScheme.error
        ReservationState.EXPIRED -> Res.string.expired to MaterialTheme.colorScheme.outline
    }

    Surface(
        color = containerColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = stringResource(textRes),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}