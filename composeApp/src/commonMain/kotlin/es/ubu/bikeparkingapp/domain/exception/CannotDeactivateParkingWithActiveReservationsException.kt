package es.ubu.bikeparkingapp.domain.exception
/**
 * Representa la excepción que se lanza cuando se intenta dar de baja a un parking
 * con reservas activas.
 */
class CannotDeactivateParkingWithActiveReservationsException : DomainException()