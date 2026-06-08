package es.ubu.bikeparkingapp.presentation.feature.theme

import es.ubu.bikeparkingapp.domain.entity.Theme

/**
 * Estado que representa la pantalla del mercado de temas.
 *
 * @property themes Lista de temas disponibles con su estado de desbloqueo.
 * @property userPoints Puntos actuales del usuario.
 * @property isLoading Indica si se están cargando los datos.
 * @property error Error ocurrido durante la carga o las operaciones, si lo hay.
 */
data class ThemesMarketplaceState(
    val themes: List<Theme> = emptyList(),
    val userPoints: Int = 0,
    val isLoading: Boolean = false,
    val error: Exception? = null
)
