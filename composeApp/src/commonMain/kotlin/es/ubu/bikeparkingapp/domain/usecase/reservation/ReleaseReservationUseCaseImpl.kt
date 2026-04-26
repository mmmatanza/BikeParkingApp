package es.ubu.bikeparkingapp.domain.usecase.reservation

import es.ubu.bikeparkingapp.domain.entity.ReservationState
import es.ubu.bikeparkingapp.domain.exception.InvalidReservationStateException
import es.ubu.bikeparkingapp.domain.exception.ReservationNotFoundException
import es.ubu.bikeparkingapp.domain.repository.ReservationRepository

/**
 * Implementación del caso de uso para liberar una reserva.
 * @property reservationRepository Repositorio de reservas.
 */
class ReleaseReservationUseCaseImpl(
    private val reservationRepository: ReservationRepository
): ReleaseReservationUseCase {
    override suspend fun invoke(reservationId: String): Result<Unit> = runCatching {
        val reservation = reservationRepository.findById(reservationId).getOrNull()
            ?: throw ReservationNotFoundException()
        // Comprobamos que la reserva se puede liberar
        if(reservation.state.canTransitionTo(ReservationState.CHECKED_OUT))
            return reservationRepository.updateState(reservationId, ReservationState.CHECKED_OUT)
        throw InvalidReservationStateException()
    }
}