package es.ubu.bikeparkingapp.domain.usecase.parking

interface ToggleOperativeStateUseCase {
    suspend operator fun invoke(parkingAreaId: String, isOperative: Boolean): Result<Unit>
}