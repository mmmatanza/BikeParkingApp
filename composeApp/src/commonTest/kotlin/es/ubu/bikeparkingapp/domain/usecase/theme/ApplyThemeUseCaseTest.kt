package es.ubu.bikeparkingapp.domain.usecase.theme

import com.russhwolf.settings.MapSettings
import es.ubu.bikeparkingapp.data.local.ThemeLocalDataSource
import es.ubu.bikeparkingapp.domain.entity.Theme
import es.ubu.bikeparkingapp.helper.repositories.FakeThemeRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplyThemeUseCaseTest {
    private lateinit var themeRepository: FakeThemeRepository
    private lateinit var themeLocalDataSource: ThemeLocalDataSource
    private lateinit var applyThemeUseCase: ApplyThemeUseCase

    @BeforeTest
    fun setup() {
        themeRepository = FakeThemeRepository()
        themeLocalDataSource = ThemeLocalDataSource(MapSettings())
        applyThemeUseCase = ApplyThemeUseCaseImpl(themeRepository, themeLocalDataSource)
    }

    @Test
    fun `invoke aplica el tema remotamente y lo guarda localmente`() = runTest {
        val theme = Theme("1", "Dark", 100, "#000000", "#FFFFFF")
        themeRepository.themes.add(theme)
        themeRepository.userThemes["user1"] = mutableListOf(theme.copy(isUnlocked = true))
        
        val result = applyThemeUseCase("user1", "1")
        
        assertTrue(result.isSuccess)
        assertTrue(themeRepository.userThemes["user1"]!!.find { it.themeId == "1" }!!.isApplied)
        assertEquals("1", themeLocalDataSource.getAppliedTheme()?.themeId)
    }

    @Test
    fun `invoke falla cuando falla la aplicacion remota`() = runTest {
        themeRepository.shouldReturnError = true
        
        val result = applyThemeUseCase("user1", "1")
        
        assertTrue(result.isFailure)
        assertEquals(null, themeLocalDataSource.getAppliedTheme())
    }
}
