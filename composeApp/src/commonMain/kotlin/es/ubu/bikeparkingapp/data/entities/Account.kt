package es.ubu.bikeparkingapp.data.entities

import kotlin.time.Instant

data class Account(
    val accoutId: String,
    val name: String,
    val role: Role,
    val createdAt: Instant,
    val updatedAt: Instant
)

enum class Role {
    ADMIN, USER
}