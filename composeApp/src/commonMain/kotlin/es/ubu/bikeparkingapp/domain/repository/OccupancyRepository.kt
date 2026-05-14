package es.ubu.bikeparkingapp.domain.repository

/**
 * Interfaz que define el repositorio de predicción de ocupación.
 */
interface OccupancyRepository {
    suspend fun getPredictedOccupancy(parkingAreaId: String): Result<Int>
}
