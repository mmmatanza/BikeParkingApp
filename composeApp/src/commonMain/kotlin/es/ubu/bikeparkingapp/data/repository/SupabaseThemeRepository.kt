package es.ubu.bikeparkingapp.data.repository

import es.ubu.bikeparkingapp.data.dto.AccountThemeDto
import es.ubu.bikeparkingapp.data.dto.ThemeDto
import es.ubu.bikeparkingapp.data.dto.UnlockThemeRequest
import es.ubu.bikeparkingapp.data.mapper.toDomain
import es.ubu.bikeparkingapp.domain.entity.Theme
import es.ubu.bikeparkingapp.domain.repository.ThemeRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

/**
 * Clase que implementa el repositorio de temas utilizando Supabase.
 */
class SupabaseThemeRepository(
    private val supabaseClient: SupabaseClient
) : ThemeRepository {

    override suspend fun getThemes(): Result<List<Theme>> = runCatching {
        supabaseClient.postgrest["themes"].select().decodeList<ThemeDto>().map { it.toDomain() }
    }

    override suspend fun getUserThemes(accountId: String): Result<List<Theme>> = runCatching {
        val allThemes = supabaseClient.postgrest["themes"].select().decodeList<ThemeDto>()
        val unlockedThemes = supabaseClient.postgrest["account_themes"]
            .select {
                filter {
                    eq("account_id", accountId)
                }
            }.decodeList<AccountThemeDto>()

        allThemes.map { themeDto ->
            val unlocked = unlockedThemes.find { it.themeId == themeDto.themeId }
            themeDto.toDomain(
                isUnlocked = unlocked != null,
                isApplied = unlocked?.isApplied ?: false
            )
        }
    }

    override suspend fun unlockTheme(accountId: String, themeId: String): Result<Unit> = runCatching {
        supabaseClient.postgrest["account_themes"].insert(
            UnlockThemeRequest(
                accountId = accountId,
                themeId = themeId,
                isApplied = false
            )
        )
    }

    override suspend fun applyTheme(accountId: String, themeId: String): Result<Unit> = runCatching {
        // Primero nos aseguramos de que están todos sin aplicar
        supabaseClient.postgrest["account_themes"].update(
            mapOf("is_applied" to false)
        ) {
            filter {
                eq("account_id", accountId)
            }
        }
        // Después aplicamos el tema
        supabaseClient.postgrest["account_themes"].update(
            mapOf("is_applied" to true)
        ) {
            filter {
                eq("account_id", accountId)
                eq("theme_id", themeId)
            }
        }
    }
}
