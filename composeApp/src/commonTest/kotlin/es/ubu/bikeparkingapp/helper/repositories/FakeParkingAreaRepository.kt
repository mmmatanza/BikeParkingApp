package es.ubu.bikeparkingapp.helper.repositories

import es.ubu.bikeparkingapp.domain.entity.ParkingArea
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import es.ubu.bikeparkingapp.domain.repository.ParkingAreaRepository

class FakeParkingAreaRepository : ParkingAreaRepository {

    private val parkingAreas = mutableListOf<ParkingArea>()
    var shouldReturnNetworkError = false

    override suspend fun getParkingAreaById(parkingId: String): Result<ParkingArea> {
        return handleFakeResponse {
            parkingAreas.find { it.parkingAreaId == parkingId }
                ?: throw Exception("Parking not found")
        }
    }

    override suspend fun getParkingAreasByOwner(ownerId: String): Result<List<ParkingArea>> {
        return handleFakeResponse {
            parkingAreas.filter { it.ownerId == ownerId }
        }
    }

    override suspend fun getNearbyParkingAreas(
        latitude: Double,
        longitude: Double,
        distance: Double
    ): Result<List<ParkingArea>> {
        return handleFakeResponse {
            parkingAreas.filter { it.isActive && it.isOperative }
        }
    }

    override suspend fun addParkingArea(parkingArea: ParkingArea): Result<Unit> {
        return handleFakeResponse {
            parkingAreas.add(parkingArea)
            Unit
        }
    }

    override suspend fun updateParkingArea(parkingArea: ParkingArea): Result<Unit> {
        return handleFakeResponse {
            val index = parkingAreas.indexOfFirst { it.parkingAreaId == parkingArea.parkingAreaId }
            if (index != -1) {
                parkingAreas[index] = parkingArea
            }
            Unit
        }
    }

    override suspend fun deactivateParkingArea(parkingId: String): Result<Unit> {
        return handleFakeResponse {
            val index = parkingAreas.indexOfFirst { it.parkingAreaId == parkingId }
            if (index != -1) {
                parkingAreas[index] = parkingAreas[index].copy(isActive = false)
            }
            Unit
        }
    }

    override suspend fun toggleOperativeState(
        parkingId: String,
        isOperative: Boolean
    ): Result<Unit> {
        return handleFakeResponse {
            val index = parkingAreas.indexOfFirst { it.parkingAreaId == parkingId }
            if (index != -1) {
                parkingAreas[index] = parkingAreas[index].copy(isOperative = isOperative)
            }
            Unit
        }
    }

    private inline fun <T> handleFakeResponse(block: () -> T): Result<T> {
        return if (shouldReturnNetworkError) {
            Result.failure(NoNetworkException())
        } else {
            runCatching { block() }
        }
    }
}