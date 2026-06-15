package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.ecodashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import es.ubu.bikeparkingapp.presentation.common.components.dialog.ErrorDialog
import org.koin.compose.viewmodel.koinViewModel

/**
 * Pantalla del Dashboard Ecológico para Administradores.
 * @param parkingAreaId ID del área de estacionamiento.
 */
class AdminEcoDashboardScreen(
    private val parkingAreaId: String
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<AdminEcoDashboardViewModel>()
        val state = viewModel.state.value

        LaunchedEffect(parkingAreaId) {
            viewModel.loadMetrics(parkingAreaId)
        }

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        state.error?.let {
            ErrorDialog(it) {
                viewModel.clearError()
            }
        }

        AdminEcoDashboardContent(
            state = state,
            onPeriodSelected = viewModel::onPeriodSelected,
            onBackClick = { navigator.pop() }
        )
    }
}
