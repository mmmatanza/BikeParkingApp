package es.ubu.bikeparkingapp.presentation.feature.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import es.ubu.bikeparkingapp.presentation.common.components.dialog.ErrorDialog
import org.koin.compose.viewmodel.koinViewModel

/**
 * Pantalla que muestra el mercado de temas.
 */
class ThemesMarketplaceScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<ThemesMarketplaceViewModel>()
        val state = viewModel.state.value

        if (state.isLoading && state.themes.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        if (state.error != null) {
            ErrorDialog(state.error) {
                viewModel.clearError()
            }
        }

        ThemesMarketplaceContent(
            state = state,
            actions = ThemesMarketplaceActions(
                onBackClick = { navigator.pop() },
                onRedeem = { viewModel.redeemTheme(it) },
                onApply = { viewModel.applyTheme(it) }
            )
        )
    }
}
