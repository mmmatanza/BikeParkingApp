package es.ubu.bikeparkingapp.helper

import es.ubu.bikeparkingapp.domain.entity.Account
import es.ubu.bikeparkingapp.domain.entity.Role
import kotlin.time.Instant

object TestData {
     val testAccount = Account(
        accountId = "user-123",
        name = "Manuel",
        taxId = "12345678A",
        role = Role.USER,
        createdAt = Instant.parse("2024-01-01T00:00:00Z"),
        updatedAt = Instant.parse("2024-01-01T00:00:00Z")
    )
}