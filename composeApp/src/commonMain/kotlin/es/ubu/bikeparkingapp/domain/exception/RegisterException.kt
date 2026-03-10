package es.ubu.bikeparkingapp.domain.exception

/**
 * Representa excepciones relacionadas con el registro.
 */
sealed class RegisterException : Exception() {
    class NameEmptyException : RegisterException()
    class TaxIdEmptyException : RegisterException()
    class PasswordMismatchException : RegisterException()
    class WeakPasswordException : RegisterException()
}