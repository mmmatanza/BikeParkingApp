package es.ubu.bikeparkingapp.data.repository

import es.ubu.bikeparkingapp.domain.entity.OccupancyPrediction
import es.ubu.bikeparkingapp.domain.repository.OccupancyRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class AnalyticsOccupancyRepository(
    private val httpClient: HttpClient,
    private val baseUrl: String = "http://url.com"
// TODO: Mover a configuración
) : OccupancyRepository {

    override suspend fun getPredictedOccupancy(parkingAreaId: String): Result<Int> = runCatching {
        val response = httpClient.get("$baseUrl/predict") {
            parameter("parking_id", parkingAreaId)
        }.body<OccupancyPrediction>()
        response.predictedOccupancy
    }
}
