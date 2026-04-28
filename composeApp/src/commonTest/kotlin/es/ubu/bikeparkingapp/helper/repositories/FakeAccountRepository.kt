package es.ubu.bikeparkingapp.helper.repositories

import es.ubu.bikeparkingapp.domain.entity.Account
import es.ubu.bikeparkingapp.domain.entity.Role
import es.ubu.bikeparkingapp.domain.repository.AccountRepository

class FakeAccountRepository : AccountRepository {

    var getAccountResult: Result<Account> = Result.failure(NotImplementedError())
    var createAccountResult: Result<Account> = Result.failure(NotImplementedError())
    var cachedAccount: Account? = null

    override suspend fun getAccount(userId: String): Result<Account> = getAccountResult

    override suspend fun createAccount(
        userId: String,
        name: String,
        taxId: String,
        role: Role
    ): Result<Account> = createAccountResult

    override suspend fun saveLocally(account: Account) {
        cachedAccount = account
    }

    override suspend fun getCachedAccount(): Account? = cachedAccount

    override suspend fun clearAccount() {
        cachedAccount = null;
    }
}