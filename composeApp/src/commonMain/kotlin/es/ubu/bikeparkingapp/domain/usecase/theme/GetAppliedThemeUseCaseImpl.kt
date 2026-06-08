package es.ubu.bikeparkingapp.domain.usecase.theme

import es.ubu.bikeparkingapp.data.local.ThemeLocalDataSource
import es.ubu.bikeparkingapp.domain.entity.Theme
import kotlinx.coroutines.flow.Flow

/**
 * Implementación del caso de uso para obtener el tema aplicado actualmente de forma reactiva.
 *
 * @property themeLocalDataSource Fuente de datos local para el tema.
 */
class GetAppliedThemeUseCaseImpl(
    private val themeLocalDataSource: ThemeLocalDataSource
) : GetAppliedThemeUseCase {
    override fun invoke(): Flow<Theme?> {
        return themeLocalDataSource.appliedTheme
    }
}
