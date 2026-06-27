package es.ubu.bikeparkingapp.helper.repositories

import es.ubu.bikeparkingapp.data.local.ThemeLocalDataSource
import es.ubu.bikeparkingapp.domain.entity.Theme
import es.ubu.bikeparkingapp.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeThemeRepository(
    private val themeLocalDataSource: ThemeLocalDataSource? = null
) : ThemeRepository {
    var themes = mutableListOf<Theme>()
    var userThemes = mutableMapOf<String, MutableList<Theme>>()
    var shouldReturnError = false
    private val _appliedThemeId = MutableStateFlow<String?>(null)

    override suspend fun getThemes(): Result<List<Theme>> {
        return if (shouldReturnError) Result.failure(Exception("Error"))
        else Result.success(themes)
    }

    override suspend fun getUserThemes(accountId: String): Result<List<Theme>> {
        return if (shouldReturnError) Result.failure(Exception("Error"))
        else Result.success(userThemes[accountId] ?: emptyList())
    }

    override suspend fun unlockTheme(accountId: String, themeId: String): Result<Unit> {
        if (shouldReturnError) return Result.failure(Exception("Error"))
        val list = userThemes[accountId] ?: return Result.failure(Exception("No themes"))
        val index = list.indexOfFirst { it.themeId == themeId }
        if (index != -1) {
            list[index] = list[index].copy(isUnlocked = true)
        }
        return Result.success(Unit)
    }

    override suspend fun applyTheme(accountId: String, themeId: String): Result<Unit> {
        if (shouldReturnError) return Result.failure(Exception("Error"))
        val list = userThemes[accountId] ?: return Result.failure(Exception("No themes"))
        userThemes[accountId] = list.map { 
            it.copy(isApplied = it.themeId == themeId)
        }.toMutableList()
        
        _appliedThemeId.value = themeId
        
        // Simular comportamiento del repositorio real
        themes.find { it.themeId == themeId }?.let { theme ->
            themeLocalDataSource?.saveAppliedTheme(theme)
        }
        
        return Result.success(Unit)
    }

    override fun getAppliedTheme(): Flow<Theme?> {
        return themeLocalDataSource?.appliedTheme ?: _appliedThemeId.map { id ->
            themes.find { it.themeId == id }
        }
    }
}
