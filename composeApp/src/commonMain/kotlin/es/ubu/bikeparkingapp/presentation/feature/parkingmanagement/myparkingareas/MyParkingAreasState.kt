package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.myparkingareas

import es.ubu.bikeparkingapp.domain.entity.ParkingArea

/**
 * Representa el estado de la pantalla de lista de parkings.
 * @property error Error ocurrido durante la carga de parkings.
 * @property parkingAreas Lista de parkings cargados.
 */
data class MyParkingAreasState (
    val isLoading: Boolean = false,
    val error: Exception? = null,
    val parkingAreas: List<ParkingArea>? = null,
    val filteredParkingAreas: List<ParkingArea>? = null,
    val searchQuery: String = ""
)