package es.ubu.bikeparkingapp.domain.usecase.parking

/**
 * Representa la interfaz del caso de uso para actualizar un parking.
 */
interface UpdateParkingAreaUseCase {
    suspend operator fun invoke(
        parkingId: String,
        ownerId: String,
        name: String,
        capacity: Int,
        openingTime: String,
        closingTime: String,
        latitude: Double,
        longitude: Double,
        rules: List<String>
    ): Result<Unit>
}