package es.ubu.bikeparkingapp.domain.usecase.auth

import es.ubu.bikeparkingapp.domain.repository.AuthRepository

/**
 * Implementación del caso de uso para manejar deep links de autenticación.
 * @property repository Repositorio de autenticación.
 */
class HandleDeepLinkUseCaseImpl(
    private val repository: AuthRepository
) : HandleDeepLinkUseCase {
    override suspend operator fun invoke(url: String): Result<Unit> =
        repository.handleDeepLink(url)
}
