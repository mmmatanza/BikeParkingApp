package es.ubu.bikeparkingapp.data.mapper

import es.ubu.bikeparkingapp.domain.exception.AccountHasActiveReservationException
import es.ubu.bikeparkingapp.domain.exception.CannotDeactivateParkingWithActiveReservationsException
import es.ubu.bikeparkingapp.domain.exception.CapacityCannotBeLowerThanOccupancyException
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import es.ubu.bikeparkingapp.domain.exception.ParkingClosingSoonException
import es.ubu.bikeparkingapp.domain.exception.ParkingHasNoFreeSpotsException
import es.ubu.bikeparkingapp.domain.exception.ParkingIsClosedException
import es.ubu.bikeparkingapp.domain.exception.ReservationExtensionBeyondClosingTimeException
import es.ubu.bikeparkingapp.domain.exception.ReservationInTimeCannotBeModifiedException
import io.github.jan.supabase.exceptions.HttpRequestException

/**
 * Clase que mapea los errores de la aplicación.
 */

object ErrorMapper {

    /**
     * Mapea una excepción a una excepción de dominio
     * @param throwable Excepción original
     * @return Excepción de dominio
     */
    fun map(throwable: Throwable): Throwable {
        if (throwable is HttpRequestException) return NoNetworkException()

        // Obtenemos el mensaje de error
        val errorMessage = throwable.message ?: ""

        return when {
            // Buscamos los códigos definidos en el trigger
            errorMessage.contains("P0001") || errorMessage.contains("ParkingHasNoFreeSpotsException") ->
                ParkingHasNoFreeSpotsException()

            errorMessage.contains("P0002") || errorMessage.contains("ExtensionExceedsClosingTimeException") ->
                ParkingClosingSoonException()

            errorMessage.contains("P0003") || errorMessage.contains("P0006") || errorMessage.contains("ExtensionDayNotOpenException") ->
                ParkingIsClosedException()

            errorMessage.contains("P0004") || errorMessage.contains("InTimeDayNotOpenException") ->
                ParkingIsClosedException()

            errorMessage.contains("P0005") || errorMessage.contains("InTimeOutOfScheduleException") ->
                ParkingIsClosedException()

            errorMessage.contains("P0007") || errorMessage.contains("InTimeCannotBeModifiedException") ->
                ReservationInTimeCannotBeModifiedException()

            errorMessage.contains("P0008") || errorMessage.contains("ExtensionBeyondClosingTimeException") ->
                ReservationExtensionBeyondClosingTimeException()

            errorMessage.contains("P0009") || errorMessage.contains("AccountHasActiveReservationException") ->
                AccountHasActiveReservationException()

            errorMessage.contains("P0010") || errorMessage.contains("CannotDeactivateParkingWithActiveReservationsException") ->
                CannotDeactivateParkingWithActiveReservationsException()

            errorMessage.contains("P0011") || errorMessage.contains("CapacityCannotBeLowerThanOccupancyException") ->
                CapacityCannotBeLowerThanOccupancyException()

            else -> throwable
        }
    }
}