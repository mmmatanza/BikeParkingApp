package es.ubu.bikeparkingapp.domain.usecase.theme

import es.ubu.bikeparkingapp.domain.entity.Theme
import es.ubu.bikeparkingapp.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow

/**
 * Implementación del caso de uso para obtener el tema aplicado actualmente de forma reactiva.
 *
 * @property themeRepository Repositorio de temas.
 */
class GetAppliedThemeUseCaseImpl(
    private val themeRepository: ThemeRepository
) : GetAppliedThemeUseCase {
    override fun invoke(): Flow<Theme?> {
        return themeRepository.getAppliedTheme()
    }
}
