package es.ubu.bikeparkingapp.domain.usecase.theme

import es.ubu.bikeparkingapp.data.local.ThemeLocalDataSource
import es.ubu.bikeparkingapp.domain.repository.ThemeRepository

/**
 * Implementación del caso de uso para aplicar un tema.
 *
 * @property themeRepository Repositorio de temas.
 * @property themeLocalDataSource Fuente de datos local para el tema.
 */
class ApplyThemeUseCaseImpl(
    private val themeRepository: ThemeRepository,
    private val themeLocalDataSource: ThemeLocalDataSource
) : ApplyThemeUseCase {
    override suspend fun invoke(accountId: String, themeId: String): Result<Unit> = runCatching {
        // Actualizar en remoto
        themeRepository.applyTheme(accountId, themeId).getOrThrow()
        
        // Obtener el objeto Theme completo para guardarlo localmente
        val themes = themeRepository.getUserThemes(accountId).getOrThrow()
        val appliedTheme = themes.find { it.themeId == themeId } 
            ?: throw Exception("Theme not found")
            
        themeLocalDataSource.saveAppliedTheme(appliedTheme)
    }
}
