package es.ubu.bikeparkingapp.domain.exception

/**
 * Representa la excepción que se lanza cuando el correo electrónico no es válido.
 */
class EmailInvalidException(cause: String? = null) : DomainException(cause)