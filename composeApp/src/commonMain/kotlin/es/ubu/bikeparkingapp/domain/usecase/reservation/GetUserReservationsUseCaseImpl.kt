package es.ubu.bikeparkingapp.domain.usecase.reservation

import es.ubu.bikeparkingapp.domain.entity.Reservation
import es.ubu.bikeparkingapp.domain.repository.ReservationRepository

/**
 * Caso de uso para obtener las reservas de un usuario.
 * @property reservationRepository Repositorio de reservas.
 * @return La lista de reservas del usuario.
 */
class GetUserReservationsUseCaseImpl(
    private val reservationRepository: ReservationRepository
) : GetUserReservationsUseCase {
    override suspend operator fun invoke(accountId: String): Result<List<Reservation>> = runCatching {
        return reservationRepository.findByAccountId(accountId)
    }
}