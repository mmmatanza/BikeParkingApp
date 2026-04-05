package es.ubu.bikeparkingapp.domain.usecase.parking

/**
 * Representa la interfaz del caso de uso para añadir un parking a un propietario.
 */
interface AddParkingAreaUseCase {
    suspend operator fun invoke(
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