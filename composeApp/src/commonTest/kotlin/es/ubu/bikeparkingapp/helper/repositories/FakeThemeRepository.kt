package es.ubu.bikeparkingapp.helper.repositories

import es.ubu.bikeparkingapp.domain.entity.Theme
import es.ubu.bikeparkingapp.domain.repository.ThemeRepository

class FakeThemeRepository : ThemeRepository {
    var themes = mutableListOf<Theme>()
    var userThemes = mutableMapOf<String, MutableList<Theme>>()
    var shouldReturnError = false

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
        return Result.success(Unit)
    }
}
