package es.ubu.bikeparkingapp.domain.exception

/**
 * Representa la clase base para las excepciones de dominio.
 * @property message Mensaje de la excepción.
 */
abstract class DomainException(message: String? = null) : Exception(message)