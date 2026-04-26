package es.ubu.bikeparkingapp.presentation.common.util

import es.ubu.bikeparkingapp.domain.exception.DomainException

/**
 * Mapeador de errores
 */
object ErrorMapper {
    fun map(error: Throwable): Exception =
        error as? DomainException ?: Exception(error.message)
}