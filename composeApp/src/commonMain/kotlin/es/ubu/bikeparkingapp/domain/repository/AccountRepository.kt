package es.ubu.bikeparkingapp.domain.repository

import es.ubu.bikeparkingapp.domain.entity.Account
import es.ubu.bikeparkingapp.domain.entity.Role

/**
 * Representa el repositorio de cuentas.
 *
 */
interface AccountRepository {
    suspend fun getAccount(userId: String): Result<Account>
    suspend fun createAccount(userId: String,
                              name: String,
                              taxId: String,
                              role: Role): Result<Account>
    suspend fun saveLocally(account: Account)
    suspend fun getCachedAccount(): Account?
    suspend fun clearAccount()
}