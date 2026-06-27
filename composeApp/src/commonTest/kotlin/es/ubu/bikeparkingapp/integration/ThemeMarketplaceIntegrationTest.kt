package es.ubu.bikeparkingapp.integration

import es.ubu.bikeparkingapp.domain.entity.Theme
import es.ubu.bikeparkingapp.domain.usecase.theme.ApplyThemeUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.theme.GetThemesMarketplaceUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.theme.RedeemPointsUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserPointsUseCaseImpl
import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.local.FakeThemeLocalDataSource
import es.ubu.bikeparkingapp.helper.repositories.FakeAccountRepository
import es.ubu.bikeparkingapp.helper.repositories.FakeThemeRepository
import es.ubu.bikeparkingapp.helper.usecases.user.FakeGetUserIdUseCase
import es.ubu.bikeparkingapp.presentation.feature.theme.ThemesMarketplaceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ThemeMarketplaceIntegrationTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var fakeThemeRepo: FakeThemeRepository
    private lateinit var fakeAccountRepo: FakeAccountRepository
    private lateinit var fakeLocalDataSource: FakeThemeLocalDataSource
    private lateinit var viewModel: ThemesMarketplaceViewModel

    private val userId = "user_theme_test"

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeLocalDataSource = FakeThemeLocalDataSource()
        fakeThemeRepo = FakeThemeRepository(fakeLocalDataSource)
        fakeAccountRepo = FakeAccountRepository()

        val getMarketplaceUseCase = GetThemesMarketplaceUseCaseImpl(fakeThemeRepo)
        val redeemUseCase = RedeemPointsUseCaseImpl(fakeThemeRepo, fakeAccountRepo)
        val applyUseCase = ApplyThemeUseCaseImpl(fakeThemeRepo)
        val getPointsUseCase = GetUserPointsUseCaseImpl(fakeAccountRepo)
        val getUserIdUseCase = FakeGetUserIdUseCase().apply { response = userId }

        viewModel = ThemesMarketplaceViewModel(
            getMarketplaceUseCase,
            redeemUseCase,
            applyUseCase,
            getPointsUseCase,
            getUserIdUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `El flujo de canjear y aplicar un tema funciona correctamente`() = runTest(testDispatcher) {

        val themeId = "theme_gold"
        val themeCost = 50
        val initialPoints = 100
        val theme = Theme(themeId, "Gold", themeCost, "#FFD700", "#000000", isUnlocked = false)
        
        fakeThemeRepo.themes.add(theme)
        fakeThemeRepo.userThemes[userId] = mutableListOf(theme)
        fakeAccountRepo.getAccountResult = Result.success(TestData.testAccount.copy(accountId = userId, points = initialPoints))

        // Cargar el mercado
        viewModel.loadMarketplace()
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(initialPoints, viewModel.state.value.userPoints)
        assertFalse(viewModel.state.value.themes.first { it.themeId == themeId }.isUnlocked)

        // Canjear el tema
        viewModel.redeemTheme(themeId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Verificar que se ha desbloqueado y los puntos han bajado
        assertEquals(initialPoints - themeCost, viewModel.state.value.userPoints)
        assertTrue(viewModel.state.value.themes.first { it.themeId == themeId }.isUnlocked)

        // Aplicar el tema
        viewModel.applyTheme(themeId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Verificar en local y remoto
        assertEquals(themeId, fakeLocalDataSource.getAppliedTheme()?.themeId)
        assertTrue(viewModel.state.value.themes.first { it.themeId == themeId }.isApplied)
    }

    @Test
    fun `No se puede canjear un tema si no hay puntos suficientes`() = runTest(testDispatcher) {
        val themeId = "theme_expensive"
        val themeCost = 1000
        val initialPoints = 10
        val theme = Theme(themeId, "Expensive", themeCost, "#000000", "#FFFFFF", isUnlocked = false)
        
        fakeThemeRepo.themes.add(theme)
        fakeThemeRepo.userThemes[userId] = mutableListOf(theme)
        fakeAccountRepo.getAccountResult = Result.success(TestData.testAccount.copy(accountId = userId, points = initialPoints))

        viewModel.loadMarketplace()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.redeemTheme(themeId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value.error != null)
        assertEquals(initialPoints, viewModel.state.value.userPoints)
        assertFalse(viewModel.state.value.themes.first { it.themeId == themeId }.isUnlocked)
    }
}
