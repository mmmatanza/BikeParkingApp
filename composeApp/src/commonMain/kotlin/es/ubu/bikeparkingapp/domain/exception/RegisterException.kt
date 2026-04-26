package es.ubu.bikeparkingapp.domain.exception

/**
 * Representa excepciones relacionadas con el registro.
 */
sealed class RegisterException : DomainException() {
    class NameEmptyException : DomainException()
    class TaxIdEmptyException : DomainException()
    class PasswordMismatchException : DomainException()
    class WeakPasswordException : DomainException()
}