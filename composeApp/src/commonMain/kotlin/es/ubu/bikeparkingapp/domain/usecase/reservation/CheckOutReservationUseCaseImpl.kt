package es.ubu.bikeparkingapp.domain.usecase.reservation

import es.ubu.bikeparkingapp.domain.entity.ReservationState
import es.ubu.bikeparkingapp.domain.exception.InvalidReservationStateException
import es.ubu.bikeparkingapp.domain.exception.ReservationNotFoundException
import es.ubu.bikeparkingapp.domain.repository.AccountRepository
import es.ubu.bikeparkingapp.domain.repository.ReservationRepository

/**
 * Caso de uso para hacer check-out en una reserva.
 * @property reservationRepository Repositorio de reservas.
 * @property accountRepository Repositorio de cuentas para actualizar los puntos.
 */
class CheckOutReservationUseCaseImpl(
    private val reservationRepository: ReservationRepository,
    private val accountRepository: AccountRepository
) : CheckOutReservationUseCase {
    override suspend fun invoke(reservationId: String): Result<Unit> = runCatching {
        // Obtener la reserva desde el repositorio
        val reservation = reservationRepository.findById(reservationId).getOrNull()
            ?: throw ReservationNotFoundException()

        if (reservation.state.canTransitionTo(ReservationState.CHECKED_OUT)) {
            reservationRepository.updateState(reservationId, ReservationState.CHECKED_OUT).getOrThrow()

            // Sumar puntos por completar la reserva
            val account = accountRepository.getAccount(reservation.accountId).getOrThrow()
            accountRepository.updatePoints(account.accountId, account.points + 2).getOrThrow()
            return Result.success(Unit)
        }
        throw InvalidReservationStateException()
    }
}