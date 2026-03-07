package es.ubu.bikeparkingapp.data.mapper

import es.ubu.bikeparkingapp.data.dto.AccountDto
import es.ubu.bikeparkingapp.domain.entity.Account
import es.ubu.bikeparkingapp.domain.entity.Role
import kotlin.time.Instant

/**
 * Representa la conversión de un objeto [AccountDto] a un objeto [Account].
 *
 */
fun AccountDto.toDomain() = Account(
    accountId = accountId,
    name = name,
    taxId = taxId,
    role = Role.fromString(role),
    createdAt = Instant.parse(createdAt),
    updatedAt = Instant.parse(updatedAt)
)