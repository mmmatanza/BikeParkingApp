package es.ubu.bikeparkingapp.data.local

import com.russhwolf.settings.Settings
import es.ubu.bikeparkingapp.domain.entity.Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

/**
 * Clase que guarda localmente la preferencia de tema aplicado.
 * Utiliza un StateFlow para permitir la reactividad en la UI.
 */
open class ThemeLocalDataSource(private val settings: Settings) {
    companion object {
        private const val KEY = "applied_theme"
    }

    private val _appliedTheme = MutableStateFlow<Theme?>(getInitialTheme())
    val appliedTheme: StateFlow<Theme?> = _appliedTheme.asStateFlow()

    private fun getInitialTheme(): Theme? {
        val raw = settings.getStringOrNull(KEY) ?: return null
        return runCatching { Json.decodeFromString<Theme>(raw) }.getOrNull()
    }

    fun saveAppliedTheme(theme: Theme) {
        settings.putString(KEY, Json.encodeToString(theme))
        _appliedTheme.value = theme
    }

    fun getAppliedTheme(): Theme? = _appliedTheme.value

    fun clear() {
        settings.remove(KEY)
        _appliedTheme.value = null
    }
}
