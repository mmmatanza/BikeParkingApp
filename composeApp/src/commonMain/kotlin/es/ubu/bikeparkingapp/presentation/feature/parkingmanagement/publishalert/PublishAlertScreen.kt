package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.publishalert

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.alert_sent_success
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import es.ubu.bikeparkingapp.presentation.common.components.dialog.ErrorDialog
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Pantalla para emitir una alerta manual a los usuarios de un parking.
 * @property parkingId Id del parking.
 */
class PublishAlertScreen(
    private val parkingId: String
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<PublishAlertViewModel>()
        val state = viewModel.state.value
        val snackbarHostState = remember { SnackbarHostState() }
        val successMessage = stringResource(Res.string.alert_sent_success)

        LaunchedEffect(state.isSuccess) {
            if (state.isSuccess) {
                navigator.pop()
            }
        }

        if (state.error != null) {
            ErrorDialog(state.error) {
                viewModel.clearError()
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            PublishAlertContent(
                state = state,
                onMessageChange = viewModel::onMessageChange,
                onSendClick = { viewModel.onPublishAlert(parkingId) },
                onBackClick = {
                    viewModel.clearState()
                    navigator.pop()
                }
            )

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
