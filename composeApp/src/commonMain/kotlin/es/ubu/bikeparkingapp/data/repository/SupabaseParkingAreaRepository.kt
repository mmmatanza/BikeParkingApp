package es.ubu.bikeparkingapp.data.repository

import es.ubu.bikeparkingapp.data.dto.ParkingAreaDto
import es.ubu.bikeparkingapp.data.mapper.toDomain
import es.ubu.bikeparkingapp.domain.entity.ParkingArea
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
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
        }.recoverCatching { cause ->
            handleException(cause)
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
        }.recoverCatching { cause ->
            handleException(cause)
        }
    }

    override suspend fun addParkingArea(parkingArea: ParkingArea):Result<Unit>{
        return runCatching {
            client.postgrest.rpc(
                function = "add_parking_area",
                parameters = buildJsonObject {
                    put("p_owner_id", parkingArea.ownerId)
                    put("p_name", parkingArea.name)
                    put("p_latitude", parkingArea.latitude)
                    put("p_longitude", parkingArea.longitude)
                    put("p_capacity", parkingArea.capacity)
                    put("p_opening_time", parkingArea.openingTime)
                    put("p_closing_time", parkingArea.closingTime)
                    put("p_rules", buildJsonArray {
                        parkingArea.rules.forEach { add(it) }
                    })
                }
            )
            Unit
        }.recoverCatching { cause ->
            handleException(cause)
        }
    }

    override suspend fun updateParkingArea(parkingArea: ParkingArea): Result<Unit> {
        return runCatching {
            client.postgrest.rpc(
                function = "update_parking_area",
                parameters = buildJsonObject {
                    put("p_parking_area_id", parkingArea.id)
                    put("p_name", parkingArea.name)
                    put("p_capacity", parkingArea.capacity)
                    put("p_opening_time", parkingArea.openingTime)
                    put("p_closing_time", parkingArea.closingTime)
                    put("p_rules", buildJsonArray {
                        parkingArea.rules.forEach { add(it) }
                    })
                }
            )
            Unit
        }.recoverCatching { cause ->
            handleException(cause)
        }
    }

    override suspend fun deactivateParkingArea(parkingId: String): Result<Unit> {
        return runCatching {
            client.from("parkingareas")
                .update({ set("is_active", false) }) {
                    filter { eq("parking_area_id", parkingId) }
                }
            Unit
        }.recoverCatching { cause ->
            handleException(cause)
        }
    }

    override suspend fun toggleOperativeState(parkingId: String, isOperative: Boolean): Result<Unit> {
        return runCatching {
            client.from("parkingareas")
                .update({ set("is_operative", isOperative) }) {
                    filter { eq("parking_area_id", parkingId) }
                }
            Unit
            }
    }

    /**
     * Centraliza el manejo de excepciones
     */
    private fun handleException(cause: Throwable): Nothing {
        val message = cause.message ?: ""
        when {
            message.contains("Unable to resolve host") ||
                    message.contains("Failed to connect") ->
                throw NoNetworkException()
            else -> throw Exception(cause)
        }
    }
}