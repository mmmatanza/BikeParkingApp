package es.ubu.bikeparkingapp.presentation.common.components.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.accept
import bikeparkingapp.composeapp.generated.resources.account_has_active_reservation
import bikeparkingapp.composeapp.generated.resources.email_invalid
import bikeparkingapp.composeapp.generated.resources.error
import bikeparkingapp.composeapp.generated.resources.generic_error
import bikeparkingapp.composeapp.generated.resources.invalid_reservation_state
import bikeparkingapp.composeapp.generated.resources.name_empty
import bikeparkingapp.composeapp.generated.resources.no_active_session
import bikeparkingapp.composeapp.generated.resources.no_internet
import bikeparkingapp.composeapp.generated.resources.parking_closing_soon
import bikeparkingapp.composeapp.generated.resources.parking_has_no_free_spots
import bikeparkingapp.composeapp.generated.resources.parking_is_closed
import bikeparkingapp.composeapp.generated.resources.password_empty
import bikeparkingapp.composeapp.generated.resources.password_mismatch
import bikeparkingapp.composeapp.generated.resources.reservation_not_found
import bikeparkingapp.composeapp.generated.resources.tax_id_empty
import bikeparkingapp.composeapp.generated.resources.weak_password
import es.ubu.bikeparkingapp.domain.exception.AccountHasActiveReservationException
import es.ubu.bikeparkingapp.domain.exception.EmailInvalidException
import es.ubu.bikeparkingapp.domain.exception.InvalidReservationStateException
import es.ubu.bikeparkingapp.domain.exception.NoActiveSessionException
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import es.ubu.bikeparkingapp.domain.exception.ParkingClosingSoonException
import es.ubu.bikeparkingapp.domain.exception.ParkingHasNoFreeSpotsException
import es.ubu.bikeparkingapp.domain.exception.ParkingIsClosedException
import es.ubu.bikeparkingapp.domain.exception.PasswordEmptyException
import es.ubu.bikeparkingapp.domain.exception.RegisterException
import es.ubu.bikeparkingapp.domain.exception.ReservationNotFoundException
import es.ubu.bikeparkingapp.presentation.common.ext.handCursor
import org.jetbrains.compose.resources.stringResource

/**
 * Muestra un diálogo de error con el mensaje del error.
 */
@Composable
fun ErrorDialog(
    error: Throwable?,
    onDismiss: () -> Unit
) {
    if (error == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss, modifier = Modifier.handCursor()) {
                Text(stringResource(Res.string.accept))
            }
        },
        title = {
            Text(stringResource(Res.string.error))
        },
        text = {
            val message = when (error) {
                is EmailInvalidException -> stringResource(Res.string.email_invalid)
                is PasswordEmptyException -> stringResource(Res.string.password_empty)
                is NoNetworkException -> stringResource(Res.string.no_internet)
                is NoActiveSessionException -> stringResource(Res.string.no_active_session)
                is RegisterException.NameEmptyException -> stringResource(Res.string.name_empty)
                is RegisterException.TaxIdEmptyException -> stringResource(Res.string.tax_id_empty)
                is RegisterException.PasswordMismatchException -> stringResource(Res.string.password_mismatch)
                is RegisterException.WeakPasswordException -> stringResource(Res.string.weak_password)
                is AccountHasActiveReservationException -> stringResource(Res.string.account_has_active_reservation)
                is ParkingClosingSoonException -> stringResource(Res.string.parking_closing_soon)
                is ParkingHasNoFreeSpotsException -> stringResource(Res.string.parking_has_no_free_spots)
                is ParkingIsClosedException -> stringResource(Res.string.parking_is_closed)
                is ReservationNotFoundException -> stringResource(Res.string.reservation_not_found)
                is InvalidReservationStateException -> stringResource(Res.string.invalid_reservation_state)
                else -> stringResource(Res.string.generic_error)
            }
            Text(text = message)
        }
    )
}