package es.ubu.bikeparkingapp.domain.entity

import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Representa a una cuenta dentro del sistema.
 *
 * @property accountId Identificador único de la cuenta.
 * @property name Nombre del usuario.
 * @property taxId DNI o CIF del usuario.
 * @property role Rol del usuario.
 * @property createdAt Fecha y hora de creación de la cuenta.
 * @property updatedAt Fecha y hora de la última actualización de la cuenta.
 */
@Serializable
data class Account(
    val accountId: String,
    val name: String,
    val taxId: String,
    val role: Role,
    val createdAt: Instant,
    val updatedAt: Instant
)

enum class Role {
    ADMIN, USER;
    companion object {
        fun fromString(value: String) =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: USER
    }
}