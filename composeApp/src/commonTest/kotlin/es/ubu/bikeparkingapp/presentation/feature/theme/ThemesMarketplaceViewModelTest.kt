package es.ubu.bikeparkingapp.presentation.feature.theme

import es.ubu.bikeparkingapp.domain.entity.Theme
import es.ubu.bikeparkingapp.helper.usecases.theme.FakeApplyThemeUseCase
import es.ubu.bikeparkingapp.helper.usecases.theme.FakeGetThemesMarketplaceUseCase
import es.ubu.bikeparkingapp.helper.usecases.theme.FakeRedeemPointsUseCase
import es.ubu.bikeparkingapp.helper.usecases.user.FakeGetUserIdUseCase
import es.ubu.bikeparkingapp.helper.usecases.user.FakeGetUserPointsUseCase
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
class ThemesMarketplaceViewModelTest {
    private lateinit var getThemesMarketplaceUseCase: FakeGetThemesMarketplaceUseCase
    private lateinit var redeemPointsUseCase: FakeRedeemPointsUseCase
    private lateinit var applyThemeUseCase: FakeApplyThemeUseCase
    private lateinit var getUserPointsUseCase: FakeGetUserPointsUseCase
    private lateinit var getUserIdUseCase: FakeGetUserIdUseCase
    private lateinit var viewModel: ThemesMarketplaceViewModel

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getThemesMarketplaceUseCase = FakeGetThemesMarketplaceUseCase()
        redeemPointsUseCase = FakeRedeemPointsUseCase()
        applyThemeUseCase = FakeApplyThemeUseCase()
        getUserPointsUseCase = FakeGetUserPointsUseCase()
        getUserIdUseCase = FakeGetUserIdUseCase()
        
        getUserIdUseCase.response = "user123"
        getThemesMarketplaceUseCase.result = Result.success(emptyList())
        getUserPointsUseCase.points = 100
        
        viewModel = ThemesMarketplaceViewModel(
            getThemesMarketplaceUseCase,
            redeemPointsUseCase,
            applyThemeUseCase,
            getUserPointsUseCase,
            getUserIdUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init carga los datos del mercado`() = runTest {
        val themes = listOf(Theme("1", "Dark", 100, "#000000", "#FFFFFF"))
        getThemesMarketplaceUseCase.result = Result.success(themes)
        getUserPointsUseCase.points = 150
        
        viewModel.loadMarketplace()
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(themes, state.themes)
        assertEquals(150, state.userPoints)
    }

    @Test
    fun `redeemTheme recarga el mercado al tener exito`() = runTest {
        redeemPointsUseCase.result = Result.success(Unit)
        
        viewModel.redeemTheme("theme1")
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `applyTheme recarga el mercado al tener exito`() = runTest {
        applyThemeUseCase.result = Result.success(Unit)
        
        viewModel.applyTheme("theme1")
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `loadMarketplace establece error al fallar`() = runTest {
        getThemesMarketplaceUseCase.result = Result.failure(Exception("Error loading"))
        
        viewModel.loadMarketplace()
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertTrue(state.error != null)
    }
}
