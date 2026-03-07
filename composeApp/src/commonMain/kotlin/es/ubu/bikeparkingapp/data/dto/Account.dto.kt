package es.ubu.bikeparkingapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Representa los datos de la cuenta obtenidos de la base de datos.
 *
 * @property accountId Identificador único de la cuenta.
 * @property name Nombre del usuario.
 * @property taxId DNI o CIF del usuario.
 * @property role Rol del usuario.
 * @property createdAt Fecha y hora de creación de la cuenta.
 * @property updatedAt Fecha y hora de la última actualización de la cuenta.
 */
@Serializable
data class AccountDto(
    // Serial name solo es necesario cuando el nombre del campo no coincide con el de la columna
    @SerialName("account_id") val accountId: String,
    val name: String,
    @SerialName("tax_id")
    val taxId: String,
    val role: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)