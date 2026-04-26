package es.ubu.bikeparkingapp.domain.exception

/**
 * Excepción que se lanza cuando se intenta extender una reserva fuera del horario de cierre.
 */
class ReservationExtensionBeyondClosingTimeException : DomainException()