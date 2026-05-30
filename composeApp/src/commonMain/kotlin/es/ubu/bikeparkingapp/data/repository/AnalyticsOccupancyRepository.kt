package es.ubu.bikeparkingapp.data.repository

import es.ubu.bikeparkingapp.domain.entity.OccupancyPrediction
import es.ubu.bikeparkingapp.domain.repository.OccupancyRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter

/**
 * Implementación del repositorio de predicción de ocupación.
 * @property httpClient Cliente HTTP para realizar las solicitudes.
 * @property supabaseClient Cliente Supabase para autenticar al usuario.
 * @property baseUrl URL base del servicio de predicción.
 */
class AnalyticsOccupancyRepository(
    private val httpClient: HttpClient,
    private val supabaseClient: SupabaseClient,
    private val baseUrl: String = "http://192.168.1.25:8000"
) : OccupancyRepository {

    override suspend fun getPredictedOccupancy(parkingAreaId: String): Result<Int> = runCatching {
        val token = supabaseClient.auth.currentAccessTokenOrNull()
            ?: throw IllegalStateException("User not authenticated")

        val response = httpClient.get("$baseUrl/predict") {
            header("Authorization", "Bearer $token")
            parameter("parking_id", parkingAreaId)
        }.body<OccupancyPrediction>()
        response.predictedOccupancy
    }
}
