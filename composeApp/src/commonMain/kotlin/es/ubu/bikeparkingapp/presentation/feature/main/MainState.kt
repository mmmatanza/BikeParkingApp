package es.ubu.bikeparkingapp.presentation.feature.main

/**
 * Representa el estado de la pantalla principal.
 *
 * @property error Mensaje de error, si ocurre uno.
 */
data class MainState (
    val error: Exception? = null
)