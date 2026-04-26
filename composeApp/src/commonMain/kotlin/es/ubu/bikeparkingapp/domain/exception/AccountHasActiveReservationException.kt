package es.ubu.bikeparkingapp.domain.exception

/**
 * Representa la excepción que se lanza cuando una cuenta ya tiene una reserva activa
 */
class AccountHasActiveReservationException(cause: String? = null) : DomainException(cause)