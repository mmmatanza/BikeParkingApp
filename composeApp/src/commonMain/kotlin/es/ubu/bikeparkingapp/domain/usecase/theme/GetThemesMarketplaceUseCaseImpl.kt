package es.ubu.bikeparkingapp.domain.usecase.theme

import es.ubu.bikeparkingapp.domain.entity.Theme
import es.ubu.bikeparkingapp.domain.repository.ThemeRepository

/**
 * Implementación del caso de uso para obtener los temas del mercado.
 *
 * @property themeRepository Repositorio de temas.
 */
class GetThemesMarketplaceUseCaseImpl(
    private val themeRepository: ThemeRepository
) : GetThemesMarketplaceUseCase {

    override suspend fun invoke(accountId: String): Result<List<Theme>> {
        return themeRepository.getUserThemes(accountId)
    }
}
