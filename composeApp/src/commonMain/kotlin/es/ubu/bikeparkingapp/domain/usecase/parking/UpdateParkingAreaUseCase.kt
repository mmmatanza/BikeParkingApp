package es.ubu.bikeparkingapp.domain.usecase.parking

import kotlinx.datetime.DayOfWeek

/**
 * Representa la interfaz del caso de uso para actualizar un parking.
 */
interface UpdateParkingAreaUseCase {
    suspend operator fun invoke(
        parkingAreaId: String,
        ownerId: String,
        name: String,
        address: String,
        capacity: Int,
        openingTime: String,
        closingTime: String,
        latitude: Double,
        longitude: Double,
        rules: List<String>,
        openDays: Set<DayOfWeek>,
        occupancyThreshold: Int? = null
    ): Result<Unit>
}