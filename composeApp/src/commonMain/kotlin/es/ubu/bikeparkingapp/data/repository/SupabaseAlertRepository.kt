package es.ubu.bikeparkingapp.data.repository

import es.ubu.bikeparkingapp.data.dto.AlertDto
import es.ubu.bikeparkingapp.data.mapper.ErrorMapper
import es.ubu.bikeparkingapp.data.mapper.toDomain
import es.ubu.bikeparkingapp.domain.entity.Alert
import es.ubu.bikeparkingapp.domain.repository.AlertRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

/**
 * Implementación del repositorio de alertas en Supabase.
 */
class SupabaseAlertRepository(
    private val client: SupabaseClient
) : AlertRepository {
    override suspend fun getAlertsByAccountId(accountId: String): Result<List<Alert>> = runCatching {
        client.from("alerts")
            .select {
                filter {
                    eq("account_id", accountId)
                }
                order("created_at", order = io.github.jan.supabase.postgrest.query.Order.DESCENDING)
            }
            .decodeList<AlertDto>()
            .map { it.toDomain() }
    }.recoverCatching { throw ErrorMapper.map(it) }

    override suspend fun markAsRead(alertId: String): Result<Unit> = runCatching {
        client.from("alerts")
            .update(mapOf("is_read" to true)) {
                filter { eq("alert_id", alertId) }
            }
        Unit
    }.recoverCatching { throw ErrorMapper.map(it) }

    override suspend fun markAllAsRead(accountId: String): Result<Unit> = runCatching {
        client.from("alerts")
            .update(mapOf("is_read" to true)) {
                filter { eq("account_id", accountId) }
            }
        Unit
    }.recoverCatching { throw ErrorMapper.map(it) }
}
