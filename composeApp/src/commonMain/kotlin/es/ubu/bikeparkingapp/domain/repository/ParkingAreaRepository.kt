package es.ubu.bikeparkingapp.domain.repository

import es.ubu.bikeparkingapp.domain.entity.ParkingArea

/**
 * Representa el repositorio de parkings
 */
interface ParkingAreaRepository {
    suspend fun getParkingAreaById(parkingId: String): Result<ParkingArea>
    suspend fun getParkingAreasByOwner(ownerId: String): Result<List<ParkingArea>>
    suspend fun getNearbyParkingAreas(
        latitude: Double,
        longitude: Double,
        distance: Double
    ): Result<List<ParkingArea>>
    suspend fun addParkingArea(parkingArea: ParkingArea): Result<Unit>
    suspend fun updateParkingArea(parkingArea: ParkingArea): Result<Unit>
    suspend fun deactivateParkingArea(parkingId: String): Result<Unit>
    suspend fun toggleOperativeState(parkingId: String, isOperative: Boolean): Result<Unit>
}