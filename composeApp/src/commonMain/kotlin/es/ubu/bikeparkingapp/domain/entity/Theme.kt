package es.ubu.bikeparkingapp.domain.entity

import kotlinx.serialization.Serializable

/**
 * Clase que representa un tema.
 * @property themeId Identificador único del tema.
 * @property name Nombre del tema.
 * @property cost Coste del tema.
 * @property primaryColor Color primario del tema.
 * @property secondaryColor Color secundario del tema.
 * @property isUnlocked Indica si el tema está desbloqueado.
 * @property isApplied Indica si el tema está aplicado.
 */

@Serializable
data class Theme(
    val themeId: String,
    val name: String,
    val cost: Int,
    val primaryColor: String,
    val secondaryColor: String,
    val isUnlocked: Boolean = false,
    val isApplied: Boolean = false
)
