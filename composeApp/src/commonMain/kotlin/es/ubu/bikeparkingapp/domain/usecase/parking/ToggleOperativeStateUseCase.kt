package es.ubu.bikeparkingapp.domain.usecase.parking

interface ToggleOperativeStateUseCase {
    suspend operator fun invoke(parkingId: String, isOperative: Boolean): Result<Unit>
}