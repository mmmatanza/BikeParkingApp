package es.ubu.bikeparkingapp.data.repository

import es.ubu.bikeparkingapp.data.dto.ParkingAreaDto
import es.ubu.bikeparkingapp.data.mapper.ErrorMapper
import es.ubu.bikeparkingapp.data.mapper.toDomain
import es.ubu.bikeparkingapp.domain.entity.ParkingArea
import es.ubu.bikeparkingapp.domain.repository.ParkingAreaRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Representa la implementación del repositorio de parkings en Supabase
 *
 * @property client Cliente Supabase
 */
class SupabaseParkingAreaRepository(
    private val client: SupabaseClient
) : ParkingAreaRepository {

    override suspend fun getParkingAreaById(parkingId: String): Result<ParkingArea> {
        return runCatching {
            client.postgrest.rpc(
                function = "get_parking_area_by_id",
                parameters = buildJsonObject { put("p_parking_area_id", parkingId) }
            ).decodeSingle<ParkingAreaDto>().toDomain()
        }.recoverCatching { throwable ->
            // Mapeamos la excepción de SQL a domain
            throw ErrorMapper.map(throwable)
        }
    }

    override suspend fun getParkingAreasByOwner(ownerId: String): Result<List<ParkingArea>> {
        return runCatching {
            client.postgrest.rpc(
                function = "get_parking_areas_by_owner",
                parameters = buildJsonObject { put("p_owner_id", ownerId) }
            )
                .decodeList<ParkingAreaDto>()
                .map { it.toDomain() }
        }.recoverCatching { throwable ->
            // Mapeamos la excepción de SQL a domain
            throw ErrorMapper.map(throwable)
        }
    }

    override suspend fun getNearbyParkingAreas(
        latitude: Double,
        longitude: Double,
        distance: Double // Distancia en metros desde la que se buscan los parkings
    ): Result<List<ParkingArea>> {
        return runCatching {
            client.postgrest.rpc(
                function = "get_nearby_parking_areas",
                parameters = buildJsonObject {
                    put("user_lat", latitude)
                    put("user_long", longitude)
                    put("radius_meters", distance)
                }
            ).decodeList<ParkingAreaDto>()
                .map { it.toDomain() }
        }.recoverCatching { throwable ->
            // Mapeamos la excepción de SQL a domain
            throw ErrorMapper.map(throwable)
        }
    }

    override suspend fun addParkingArea(parkingArea: ParkingArea): Result<Unit> {
        return runCatching {
            client.postgrest.rpc(
                function = "add_parking_area",
                parameters = buildJsonObject {
                    put("p_owner_id", parkingArea.ownerId)
                    put("p_name", parkingArea.name)
                    put("p_address", parkingArea.address)
                    put("p_latitude", parkingArea.latitude)
                    put("p_longitude", parkingArea.longitude)
                    put("p_capacity", parkingArea.capacity)
                    put("p_timezone_id", parkingArea.timezoneId)
                    put("p_opening_time", parkingArea.openingTime)
                    put("p_closing_time", parkingArea.closingTime)
                    put("p_open_days", buildJsonArray {
                        parkingArea.openDays
                            .map { it.ordinal }
                            .sorted()
                            .forEach { add(it) }
                    })
                    put("p_rules", buildJsonArray {
                        parkingArea.rules.forEach { add(it) }
                    })
                    put("p_occupancy_threshold", parkingArea.occupancyThreshold)
                }
            )
            Unit
        }.recoverCatching { throwable ->
            // Mapeamos la excepción de SQL a domain
            throw ErrorMapper.map(throwable)
        }
    }

    override suspend fun updateParkingArea(parkingArea: ParkingArea): Result<Unit> {
        return runCatching {
            client.postgrest.rpc(
                function = "update_parking_area",
                parameters = buildJsonObject {
                    put("p_parking_area_id", parkingArea.parkingAreaId)
                    put("p_name", parkingArea.name)
                    put("p_address", parkingArea.address)
                    put("p_latitude", parkingArea.latitude)
                    put("p_longitude", parkingArea.longitude)
                    put("p_capacity", parkingArea.capacity)
                    put("p_timezone_id", parkingArea.timezoneId)
                    put("p_opening_time", parkingArea.openingTime)
                    put("p_closing_time", parkingArea.closingTime)
                    put("p_open_days", buildJsonArray {
                        parkingArea.openDays
                            .map { it.ordinal }
                            .sorted()
                            .forEach { add(it) }
                    })
                    put("p_rules", buildJsonArray {
                        parkingArea.rules.forEach { add(it) }
                    })
                    put("p_occupancy_threshold", parkingArea.occupancyThreshold)
                }
            )
            Unit
        }.recoverCatching { throwable ->
            // Mapeamos la excepción de SQL a domain
            throw ErrorMapper.map(throwable)
        }
    }

    override suspend fun deactivateParkingArea(parkingId: String): Result<Unit> {
        return runCatching {
            client.from("parkingareas")
                .update({ set("is_active", false) }) {
                    filter { eq("parking_area_id", parkingId) }
                }
            Unit
        }.recoverCatching { throwable ->
            // Mapeamos la excepción de SQL a domain
            throw ErrorMapper.map(throwable)
        }
    }

    override suspend fun toggleOperativeState(
        parkingId: String,
        isOperative: Boolean
    ): Result<Unit> {
        return runCatching {
            client.from("parkingareas")
                .update({ set("is_operative", isOperative) }) {
                    filter { eq("parking_area_id", parkingId) }
                }
            Unit
        }.recoverCatching { throwable ->
            // Mapeamos la excepción de SQL a domain
            throw ErrorMapper.map(throwable)
        }
    }

}