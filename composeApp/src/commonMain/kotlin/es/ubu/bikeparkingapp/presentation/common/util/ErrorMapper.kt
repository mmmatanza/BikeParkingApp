package es.ubu.bikeparkingapp.presentation.common.util

import es.ubu.bikeparkingapp.domain.exception.AccountHasActiveReservationException
import es.ubu.bikeparkingapp.domain.exception.InvalidReservationStateException
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import es.ubu.bikeparkingapp.domain.exception.ParkingClosingSoonException
import es.ubu.bikeparkingapp.domain.exception.ParkingHasNoFreeSpotsException
import es.ubu.bikeparkingapp.domain.exception.ParkingIsClosedException
import es.ubu.bikeparkingapp.domain.exception.ReservationNotFoundException

/**
 * Mapeador de errores
 */
object ErrorMapper {
    private val knownDomainExceptions = setOf(
        NoNetworkException::class,
        AccountHasActiveReservationException::class,
        ParkingClosingSoonException::class,
        ParkingHasNoFreeSpotsException::class,
        ParkingIsClosedException::class,
        ReservationNotFoundException::class,
        InvalidReservationStateException::class
    )

    fun map(error: Throwable): Exception =
        if (error is Exception && knownDomainExceptions.any { it.isInstance(error) }) error
        else Exception(error.message)
}