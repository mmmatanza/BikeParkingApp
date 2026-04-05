package es.ubu.bikeparkingapp.presentation.feature.main

import es.ubu.bikeparkingapp.domain.entity.Role

/**
 * Representa el estado de la pantalla principal.
 * @property userRole Rol del usuario.
 * @property error Mensaje de error, si ocurre uno.
 */
data class MainState(
    val userRole: Role? = null,
    val error: Exception? = null
)