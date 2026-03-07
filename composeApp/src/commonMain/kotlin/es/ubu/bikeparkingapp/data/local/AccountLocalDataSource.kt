package es.ubu.bikeparkingapp.data.local

import com.russhwolf.settings.Settings
import es.ubu.bikeparkingapp.domain.entity.Account
import kotlinx.serialization.json.Json

/**
 * Representa la fuente de datos local para persistir los datos de la cuenta.
 *
 * @property settings Settings para persistir los datos.
 */
class AccountLocalDataSource(private val settings: Settings) {
    companion object {
        private const val KEY = "cached_account"
    }

    fun save(account: Account) {
        settings.putString(KEY, Json.encodeToString(account))
    }

    fun get(): Account? {
        val raw = settings.getStringOrNull(KEY) ?: return null
        return runCatching { Json.decodeFromString<Account>(raw) }.getOrNull()
    }

    fun clear() {
        settings.remove(KEY)
    }
}