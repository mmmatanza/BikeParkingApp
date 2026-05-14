package es.ubu.bikeparkingapp.domain.usecase.parking

import es.ubu.bikeparkingapp.domain.entity.ParkingDiscovery
import es.ubu.bikeparkingapp.domain.entity.isOpen
import es.ubu.bikeparkingapp.domain.repository.AuthRepository
import es.ubu.bikeparkingapp.domain.repository.ParkingAreaRepository
import es.ubu.bikeparkingapp.domain.repository.ReservationRepository
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Implementación del caso de uso para obtener los parkings cercanos y el recomendado.
 */
class GetParkingDiscoveryUseCaseImpl(
    private val parkingAreaRepository: ParkingAreaRepository,
    private val reservationRepository: ReservationRepository,
    private val authRepository: AuthRepository
) : GetParkingDiscoveryUseCase {

    override suspend fun invoke(
        latitude: Double,
        longitude: Double,
        distance: Double
    ): Result<ParkingDiscovery> = runCatching {
        val userId = authRepository.getCurrentUserId().getOrThrow()
        val nearbyAreas = parkingAreaRepository.getNearbyParkingAreas(latitude, longitude, distance).getOrThrow()

        if (nearbyAreas.isEmpty()) {
            return@runCatching ParkingDiscovery(recommended = null, allNearby = emptyList())
        }

        // Filtramos solo los que están abiertos
        val openAreas = nearbyAreas.filter { it.isOpen() }

        if (openAreas.isEmpty()) {
            return@runCatching ParkingDiscovery(recommended = null, allNearby = nearbyAreas)
        }

        // Valorar el mejor parking
        val recommended = openAreas.maxByOrNull { area ->
            val dist = calculateDistance(latitude, longitude, area.latitude, area.longitude)
            val visits = reservationRepository.countCompletedReservationsByUserInParking(
                userId,
                area.parkingAreaId ?: ""
            ).getOrDefault(0)

            // Fórmula de puntuación
            // Cada visita suma 500 puntos, y se penaliza con la distancia
            (visits * 500.0) - dist
        }

        ParkingDiscovery(
            recommended = recommended,
            allNearby = nearbyAreas.filter { it.parkingAreaId != recommended?.parkingAreaId }
        )
    }

    /**
     * Calcula la distancia entre dos puntos geográficos usando la fórmula de Haversine.
     * Retorna la distancia en metros.
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0 // Radio de la Tierra en metros
        val dLat = (lat2 - lat1).toRadians()
        val dLon = (lon2 - lon1).toRadians()
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1.toRadians()) * cos(lat2.toRadians()) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    private fun Double.toRadians(): Double = this * PI / 180.0
}
