package es.ubu.bikeparkingapp.domain.usecase.theme

import es.ubu.bikeparkingapp.domain.entity.Theme
import es.ubu.bikeparkingapp.helper.repositories.FakeThemeRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetThemesMarketplaceUseCaseTest {
    private lateinit var themeRepository: FakeThemeRepository
    private lateinit var getThemesMarketplaceUseCase: GetThemesMarketplaceUseCase

    @BeforeTest
    fun setup() {
        themeRepository = FakeThemeRepository()
        getThemesMarketplaceUseCase = GetThemesMarketplaceUseCaseImpl(themeRepository)
    }

    @Test
    fun `invoke devuelve temas del repositorio`() = runTest {
        val theme = Theme("1", "Dark", 100, "#000000", "#FFFFFF")
        themeRepository.userThemes["user1"] = mutableListOf(theme)
        
        val result = getThemesMarketplaceUseCase("user1")
        
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().size)
        assertEquals("Dark", result.getOrThrow()[0].name)
    }

    @Test
    fun `invoke devuelve error cuando el repositorio falla`() = runTest {
        themeRepository.shouldReturnError = true
        
        val result = getThemesMarketplaceUseCase("user1")
        
        assertTrue(result.isFailure)
    }
}
