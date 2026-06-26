package es.ubu.bikeparkingapp.presentation.common.ext

import androidx.compose.runtime.Composable
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.account_has_active_reservation
import bikeparkingapp.composeapp.generated.resources.cannot_deactivate_parking_with_active_reservations
import bikeparkingapp.composeapp.generated.resources.capacity_cannot_be_lower_than_occupancy
import bikeparkingapp.composeapp.generated.resources.chat_error_service
import bikeparkingapp.composeapp.generated.resources.email_invalid
import bikeparkingapp.composeapp.generated.resources.extension_exceeds_closing
import bikeparkingapp.composeapp.generated.resources.generic_error
import bikeparkingapp.composeapp.generated.resources.invalid_reservation_state
import bikeparkingapp.composeapp.generated.resources.name_empty
import bikeparkingapp.composeapp.generated.resources.no_active_session
import bikeparkingapp.composeapp.generated.resources.no_internet
import bikeparkingapp.composeapp.generated.resources.parking_closing_soon
import bikeparkingapp.composeapp.generated.resources.parking_has_no_free_spots
import bikeparkingapp.composeapp.generated.resources.parking_is_closed
import bikeparkingapp.composeapp.generated.resources.password_mismatch
import bikeparkingapp.composeapp.generated.resources.reservation_in_time_cannot_be_modified
import bikeparkingapp.composeapp.generated.resources.reservation_not_found
import bikeparkingapp.composeapp.generated.resources.tax_id_empty
import bikeparkingapp.composeapp.generated.resources.weak_password
import es.ubu.bikeparkingapp.domain.exception.AccountHasActiveReservationException
import es.ubu.bikeparkingapp.domain.exception.CannotDeactivateParkingWithActiveReservationsException
import es.ubu.bikeparkingapp.domain.exception.CapacityCannotBeLowerThanOccupancyException
import es.ubu.bikeparkingapp.domain.exception.ChatServiceUnavailableException
import es.ubu.bikeparkingapp.domain.exception.EmailInvalidException
import es.ubu.bikeparkingapp.domain.exception.InvalidReservationStateException
import es.ubu.bikeparkingapp.domain.exception.NoActiveSessionException
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import es.ubu.bikeparkingapp.domain.exception.ParkingClosingSoonException
import es.ubu.bikeparkingapp.domain.exception.ParkingHasNoFreeSpotsException
import es.ubu.bikeparkingapp.domain.exception.ParkingIsClosedException
import es.ubu.bikeparkingapp.domain.exception.RegisterException
import es.ubu.bikeparkingapp.domain.exception.ReservationExtensionBeyondClosingTimeException
import es.ubu.bikeparkingapp.domain.exception.ReservationInTimeCannotBeModifiedException
import es.ubu.bikeparkingapp.domain.exception.ReservationNotFoundException
import org.jetbrains.compose.resources.stringResource


/**
 * Convierte cualquier Throwable en un String legible.
 */
@Composable
fun Throwable.toUserMessage(): String {
    val resource = when (this) {
        is EmailInvalidException -> Res.string.email_invalid
        is NoNetworkException -> Res.string.no_internet
        is RegisterException.NameEmptyException -> Res.string.name_empty
        is RegisterException.TaxIdEmptyException -> Res.string.tax_id_empty
        is RegisterException.PasswordMismatchException -> Res.string.password_mismatch
        is RegisterException.WeakPasswordException -> Res.string.weak_password
        is AccountHasActiveReservationException -> Res.string.account_has_active_reservation
        is ParkingClosingSoonException -> Res.string.parking_closing_soon
        is ParkingHasNoFreeSpotsException -> Res.string.parking_has_no_free_spots
        is ParkingIsClosedException -> Res.string.parking_is_closed
        is ReservationNotFoundException -> Res.string.reservation_not_found
        is InvalidReservationStateException -> Res.string.invalid_reservation_state
        is ReservationExtensionBeyondClosingTimeException -> Res.string.extension_exceeds_closing
        is ReservationInTimeCannotBeModifiedException -> Res.string.reservation_in_time_cannot_be_modified
        is CannotDeactivateParkingWithActiveReservationsException -> Res.string.cannot_deactivate_parking_with_active_reservations
        is CapacityCannotBeLowerThanOccupancyException -> Res.string.capacity_cannot_be_lower_than_occupancy
        is ChatServiceUnavailableException -> Res.string.chat_error_service
        is NoActiveSessionException -> Res.string.no_active_session
        else -> null
    }

    return if (resource != null) {
        stringResource(resource)
    } else {
        // Si no es una excepción con mensaje concreto, usamos el mensaje genérico
        this.message ?: stringResource(Res.string.generic_error)
    }
}