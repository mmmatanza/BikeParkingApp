package es.ubu.bikeparkingapp.domain.usecase.theme

import es.ubu.bikeparkingapp.domain.entity.Theme
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz que define el caso de uso para obtener el tema aplicado actualmente de forma reactiva.
 */
interface GetAppliedThemeUseCase {
    operator fun invoke(): Flow<Theme?>
}
