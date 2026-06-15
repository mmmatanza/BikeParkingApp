package es.ubu.bikeparkingapp.presentation.feature.myimpact

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel

/**
 * Pantalla que envuelve el contenido del dashboard de impacto ecológico del usuario.
 */
class UserEcoDashboardScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<UserEcoDashboardViewModel>()
        val state = viewModel.state.value

        UserEcoDashboardContent(
            state = state,
            onPeriodSelected = viewModel::onPeriodSelected,
            onBackClick = { navigator.pop() }
        )
    }
}
