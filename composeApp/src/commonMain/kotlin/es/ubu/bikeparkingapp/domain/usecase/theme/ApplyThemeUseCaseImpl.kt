package es.ubu.bikeparkingapp.domain.usecase.theme

import es.ubu.bikeparkingapp.domain.repository.ThemeRepository

/**
 * Implementación del caso de uso para aplicar un tema.
 *
 * @property themeRepository Repositorio de temas.
 */
class ApplyThemeUseCaseImpl(
    private val themeRepository: ThemeRepository
) : ApplyThemeUseCase {
    override suspend fun invoke(accountId: String, themeId: String): Result<Unit> =
        themeRepository.applyTheme(accountId, themeId)
}
