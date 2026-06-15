package es.ubu.bikeparkingapp.domain.usecase.theme

import es.ubu.bikeparkingapp.domain.entity.Theme
import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.repositories.FakeAccountRepository
import es.ubu.bikeparkingapp.helper.repositories.FakeThemeRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RedeemPointsUseCaseTest {
    private lateinit var themeRepository: FakeThemeRepository
    private lateinit var accountRepository: FakeAccountRepository
    private lateinit var redeemPointsUseCase: RedeemPointsUseCase

    @BeforeTest
    fun setup() {
        themeRepository = FakeThemeRepository()
        accountRepository = FakeAccountRepository()
        redeemPointsUseCase = RedeemPointsUseCaseImpl(themeRepository, accountRepository)
    }

    @Test
    fun `invoke desbloquea el tema y actualiza los puntos al tener exito`() = runTest {
        val theme = Theme("1", "Dark", 100, "#000000", "#FFFFFF", isUnlocked = false)
        themeRepository.themes.add(theme)
        themeRepository.userThemes["user1"] = mutableListOf(theme)
        accountRepository.getAccountResult = Result.success(TestData.testAccount.copy(points = 150))
        
        val result = redeemPointsUseCase("user1", "1")
        
        assertTrue(result.isSuccess)
        assertTrue(themeRepository.userThemes["user1"]!!.any { it.themeId == "1" && it.isUnlocked })
        assertEquals(accountRepository.getAccountResult.getOrThrow().points, 50)
    }

    @Test
    fun `invoke falla cuando no hay suficientes puntos`() = runTest {
        val theme = Theme("1", "Dark", 100, "#000000", "#FFFFFF", isUnlocked = false)
        themeRepository.themes.add(theme)
        themeRepository.userThemes["user1"] = mutableListOf(theme)
        accountRepository.getAccountResult = Result.success(TestData.testAccount.copy(points = 50))
        
        val result = redeemPointsUseCase("user1", "1")
        
        assertTrue(result.isFailure)
    }

    @Test
    fun `invoke falla cuando el tema ya esta desbloqueado`() = runTest {
        val theme = Theme("1", "Dark", 100, "#000000", "#FFFFFF", isUnlocked = true)
        themeRepository.themes.add(theme)
        themeRepository.userThemes["user1"] = mutableListOf(theme)
        accountRepository.getAccountResult = Result.success(TestData.testAccount.copy(points = 150))
        
        val result = redeemPointsUseCase("user1", "1")
        
        assertTrue(result.isFailure)
    }
}
