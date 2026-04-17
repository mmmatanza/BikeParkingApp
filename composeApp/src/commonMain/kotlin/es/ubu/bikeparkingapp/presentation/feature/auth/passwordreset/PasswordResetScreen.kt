package es.ubu.bikeparkingapp.presentation.feature.auth.passwordreset

import androidx.compose.runtime.Composable
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.accept
import bikeparkingapp.composeapp.generated.resources.attention
import bikeparkingapp.composeapp.generated.resources.reset_advice
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import es.ubu.bikeparkingapp.presentation.common.components.dialog.ErrorDialog
import es.ubu.bikeparkingapp.presentation.common.components.dialog.InformationDialog
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Representa la pantalla de restablecer contraseña.
 */
class PasswordResetScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<PasswordResetViewModel>()
        val state = viewModel.state.value

        // Diálogo para informar del envío de correo
        InformationDialog(
            isVisible = state.success,
            title = stringResource(Res.string.attention),
            message = stringResource(Res.string.reset_advice),
            buttonText = stringResource(Res.string.accept),
            onDismiss = viewModel::clearSuccess
        )

        if (state.error != null) {
            ErrorDialog(state.error) {
                viewModel.clearError()
            }
        }

        PasswordResetContent(
            state,
            onPasswordResetClick = viewModel::onPasswordResetClick,
            onEmailChange = viewModel::onEmailChange,
            onBackClick = navigator::pop
        )
    }
}