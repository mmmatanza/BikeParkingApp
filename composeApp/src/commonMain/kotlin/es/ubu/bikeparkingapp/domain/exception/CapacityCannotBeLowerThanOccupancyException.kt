package es.ubu.bikeparkingapp.domain.exception

/**
 * Excepción lanzada cuando se intenta establecer una capacidad inferior a la ocupación actual.
 */
class CapacityCannotBeLowerThanOccupancyException : DomainException()