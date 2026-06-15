package es.ubu.bikeparkingapp.presentation.feature.alerts

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.alert_abnormal_booking_pattern_msg
import bikeparkingapp.composeapp.generated.resources.alert_abnormal_booking_pattern_title
import bikeparkingapp.composeapp.generated.resources.alert_occupancy_limit
import bikeparkingapp.composeapp.generated.resources.alert_occupancy_limit_title
import bikeparkingapp.composeapp.generated.resources.alert_parking_notification_title
import bikeparkingapp.composeapp.generated.resources.alert_predicted_occupancy
import bikeparkingapp.composeapp.generated.resources.alert_predicted_occupancy_title
import bikeparkingapp.composeapp.generated.resources.alert_recurrent_cancellations_msg
import bikeparkingapp.composeapp.generated.resources.alert_recurrent_cancellations_title
import bikeparkingapp.composeapp.generated.resources.alert_recurrent_expired_msg
import bikeparkingapp.composeapp.generated.resources.alert_recurrent_expired_title
import bikeparkingapp.composeapp.generated.resources.alert_recurrent_overstay_msg
import bikeparkingapp.composeapp.generated.resources.alert_recurrent_overstay_title
import bikeparkingapp.composeapp.generated.resources.alert_suspicious_new_account_msg
import bikeparkingapp.composeapp.generated.resources.alert_suspicious_new_account_title
import bikeparkingapp.composeapp.generated.resources.alert_suspicious_reservation
import bikeparkingapp.composeapp.generated.resources.alert_suspicious_reservation_title
import bikeparkingapp.composeapp.generated.resources.alert_unusual_booking_frequency_msg
import bikeparkingapp.composeapp.generated.resources.alert_unusual_booking_frequency_title
import bikeparkingapp.composeapp.generated.resources.alert_unusual_booking_hour_msg
import bikeparkingapp.composeapp.generated.resources.alert_unusual_booking_hour_title
import bikeparkingapp.composeapp.generated.resources.alert_unusual_booking_weekday_msg
import bikeparkingapp.composeapp.generated.resources.alert_unusual_booking_weekday_title
import bikeparkingapp.composeapp.generated.resources.mark_as_read
import bikeparkingapp.composeapp.generated.resources.no_alerts
import es.ubu.bikeparkingapp.domain.entity.Alert
import es.ubu.bikeparkingapp.domain.entity.AlertType
import es.ubu.bikeparkingapp.presentation.common.util.formatInstant
import org.jetbrains.compose.resources.stringResource

@Composable
fun AlertsContent(
    state: AlertsState,
    onMarkAsRead: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading && state.alerts.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (state.alerts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(Res.string.no_alerts))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.alerts) { alert ->
                    AlertItem(alert, onMarkAsRead)
                }
            }
        }
    }
}

@Composable
fun AlertItem(
    alert: Alert,
    onMarkAsRead: (String) -> Unit
) {
    val backgroundColor = if (alert.isRead) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (alert.isRead) 0.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = if (alert.isRead) Color.Gray else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = getAlertTitle(alert),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (alert.isRead) Color.Gray else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = getAlertMessage(alert),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (alert.isRead) Color.Gray else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!alert.isRead) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onMarkAsRead(alert.alertId) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(Res.string.mark_as_read))
                }
            }
        }
    }
}

@Composable
private fun getAlertTitle(alert: Alert): String {
    return when (alert.type) {
        AlertType.OCCUPANCY_LIMIT -> stringResource(Res.string.alert_occupancy_limit_title)
        AlertType.PREDICTED_OCCUPANCY -> stringResource(Res.string.alert_predicted_occupancy_title)
        AlertType.SUSPICIOUS_RESERVATION -> stringResource(Res.string.alert_suspicious_reservation_title)
        AlertType.PARKING_NOTIFICATION -> stringResource(Res.string.alert_parking_notification_title)
        AlertType.RECURRENT_EXPIRED -> stringResource(Res.string.alert_recurrent_expired_title)
        AlertType.RECURRENT_CANCELLATIONS -> stringResource(Res.string.alert_recurrent_cancellations_title)
        AlertType.RECURRENT_OVERSTAY -> stringResource(Res.string.alert_recurrent_overstay_title)
        AlertType.SUSPICIOUS_NEW_ACCOUNT -> stringResource(Res.string.alert_suspicious_new_account_title)
        AlertType.UNUSUAL_BOOKING_FREQUENCY -> stringResource(Res.string.alert_unusual_booking_frequency_title)
        AlertType.UNUSUAL_BOOKING_HOUR -> stringResource(Res.string.alert_unusual_booking_hour_title)
        AlertType.UNUSUAL_BOOKING_WEEKDAY -> stringResource(Res.string.alert_unusual_booking_weekday_title)
        AlertType.ABNORMAL_BOOKING_PATTERN -> stringResource(Res.string.alert_abnormal_booking_pattern_title)
    }
}

@Composable
private fun getAlertMessage(alert: Alert): String {
    val parkingDisplay = alert.parkingName ?: alert.parkingAreaId ?: ""
    return when (alert.type) {
        AlertType.OCCUPANCY_LIMIT -> {
            val value = alert.value?.let { (it * 10).toInt() / 10.0 } ?: 0.0
            stringResource(Res.string.alert_occupancy_limit, "$value%", parkingDisplay)
        }
        AlertType.PREDICTED_OCCUPANCY -> {
            val value = alert.value?.let { (it * 10).toInt() / 10.0 } ?: 0.0
            stringResource(Res.string.alert_predicted_occupancy, "$value%", parkingDisplay)
        }
        AlertType.SUSPICIOUS_RESERVATION -> stringResource(
            Res.string.alert_suspicious_reservation,
            alert.reservationId ?: ""
        )
        AlertType.PARKING_NOTIFICATION -> alert.customMessage ?: ""
        AlertType.RECURRENT_EXPIRED -> stringResource(
            Res.string.alert_recurrent_expired_msg,
            parkingDisplay,
            alert.reservationId ?: "",
            formatInstant(alert.createdAt)
        )
        AlertType.RECURRENT_CANCELLATIONS -> stringResource(
            Res.string.alert_recurrent_cancellations_msg,
            parkingDisplay,
            alert.reservationId ?: "",
            formatInstant(alert.createdAt)
        )
        AlertType.RECURRENT_OVERSTAY -> stringResource(
            Res.string.alert_recurrent_overstay_msg,
            parkingDisplay,
            alert.reservationId ?: "",
            formatInstant(alert.createdAt)
        )
        AlertType.SUSPICIOUS_NEW_ACCOUNT -> stringResource(
            Res.string.alert_suspicious_new_account_msg,
            parkingDisplay,
            alert.reservationId ?: "",
            formatInstant(alert.createdAt)
        )
        AlertType.UNUSUAL_BOOKING_FREQUENCY -> stringResource(
            Res.string.alert_unusual_booking_frequency_msg,
            parkingDisplay,
            alert.reservationId ?: "",
            formatInstant(alert.createdAt)
        )
        AlertType.UNUSUAL_BOOKING_HOUR -> stringResource(
            Res.string.alert_unusual_booking_hour_msg,
            parkingDisplay,
            alert.reservationId ?: "",
            formatInstant(alert.createdAt)
        )
        AlertType.UNUSUAL_BOOKING_WEEKDAY -> stringResource(
            Res.string.alert_unusual_booking_weekday_msg,
            parkingDisplay,
            alert.reservationId ?: "",
            formatInstant(alert.createdAt)
        )
        AlertType.ABNORMAL_BOOKING_PATTERN -> stringResource(
            Res.string.alert_abnormal_booking_pattern_msg,
            parkingDisplay,
            alert.reservationId ?: "",
            formatInstant(alert.createdAt)
        )
    }
}
