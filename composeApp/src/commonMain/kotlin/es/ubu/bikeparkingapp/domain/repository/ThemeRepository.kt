package es.ubu.bikeparkingapp.domain.repository

import es.ubu.bikeparkingapp.domain.entity.Theme

/**
 * Interfaz que define el repositorio de temas.
 */

interface ThemeRepository {
    suspend fun getThemes(): Result<List<Theme>>
    suspend fun getUserThemes(accountId: String): Result<List<Theme>>
    suspend fun unlockTheme(accountId: String, themeId: String): Result<Unit>
    suspend fun applyTheme(accountId: String, themeId: String): Result<Unit>
}
