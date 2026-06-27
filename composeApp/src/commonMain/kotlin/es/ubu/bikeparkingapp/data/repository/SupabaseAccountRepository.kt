package es.ubu.bikeparkingapp.data.repository

import es.ubu.bikeparkingapp.data.dto.AccountDto
import es.ubu.bikeparkingapp.data.local.AccountLocalDataSource
import es.ubu.bikeparkingapp.data.mapper.ErrorMapper
import es.ubu.bikeparkingapp.data.mapper.toDomain
import es.ubu.bikeparkingapp.domain.entity.Account
import es.ubu.bikeparkingapp.domain.entity.Role
import es.ubu.bikeparkingapp.domain.repository.AccountRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from


/**
 * Representa la implementación del repositorio de cuentas en Supabase.
 *
 * @property client Cliente Supabase para interactuar con la base de datos.
 * @property localDataSource Fuente de datos local para persistir los datos.
 */
class SupabaseAccountRepository(
    private val client: SupabaseClient,
    private val localDataSource: AccountLocalDataSource
): AccountRepository {

    override suspend fun getAccount(userId: String): Result<Account> {
        return runCatching {
            client.from("accounts")
                .select() {
                    filter { eq("account_id", userId) }
                }
                .decodeSingle<AccountDto>()
                .toDomain()
        }.onFailure { throw ErrorMapper.map(it) }
    }

    override suspend fun createAccount(
        userId: String,
        name: String,
        taxId: String,
        role: Role
    ): Result<Account> {
        return runCatching {
            client.from("accounts")
                .insert(
                    mapOf(
                        "account_id" to userId,
                        "name" to name,
                        "tax_id" to taxId,
                        "role" to role.name.lowercase()
                    )
                ) {
                    select()
                }
                .decodeSingle<AccountDto>()
                .toDomain()
        }.onFailure { throw ErrorMapper.map(it) }
    }

    override suspend fun updatePoints(accountId: String, points: Int): Result<Unit> {
        return runCatching {
            client.from("accounts").update(
                mapOf("points" to points)
            ) {
                filter { eq("account_id", accountId) }
            }
            Unit
        }.onFailure { throw ErrorMapper.map(it) }
    }

    override suspend fun saveLocally(account: Account) {
        localDataSource.save(account)
    }

    override suspend fun getCachedAccount(): Account? {
        return localDataSource.get()
    }

    override suspend fun clearAccount() {
        localDataSource.clear()
    }

}
