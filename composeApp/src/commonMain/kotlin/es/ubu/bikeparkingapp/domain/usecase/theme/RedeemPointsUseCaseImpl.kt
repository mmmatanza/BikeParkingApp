package es.ubu.bikeparkingapp.domain.usecase.theme

import es.ubu.bikeparkingapp.domain.repository.AccountRepository
import es.ubu.bikeparkingapp.domain.repository.ThemeRepository

/**
 * Clase que implementa el caso de uso para canjear los puntos del usuario
 */
class RedeemPointsUseCaseImpl(
    private val themeRepository: ThemeRepository,
    private val accountRepository: AccountRepository
) : RedeemPointsUseCase {
    override suspend fun invoke(accountId: String, themeId: String): Result<Unit> = runCatching {
        val account = accountRepository.getAccount(accountId).getOrThrow()
        val themes = themeRepository.getUserThemes(accountId).getOrThrow()
        val theme = themes.find { it.themeId == themeId } ?: throw Exception("Theme not found")

        if (theme.isUnlocked) throw Exception("Theme already unlocked")
        if (account.points < theme.cost) throw Exception("Insufficient points")

        // Desbloquea primero el tema
        themeRepository.unlockTheme(accountId, themeId).getOrThrow()
        
        // Quita los puntos
        accountRepository.updatePoints(accountId, account.points - theme.cost).getOrThrow()
    }
}
