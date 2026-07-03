package es.ubu.bikeparkingapp.presentation.feature.auth.passwordreset

import androidx.compose.runtime.Composable
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.accept
import bikeparkingapp.composeapp.generated.resources.attention
import bikeparkingapp.composeapp.generated.resources.password_updated_success
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import es.ubu.bikeparkingapp.presentation.common.components.dialog.ErrorDialog
import es.ubu.bikeparkingapp.presentation.common.components.dialog.InformationDialog
import es.ubu.bikeparkingapp.presentation.feature.auth.login.LoginScreen
import es.ubu.bikeparkingapp.presentation.feature.auth.newpassword.NewPasswordContent
import es.ubu.bikeparkingapp.presentation.feature.auth.newpassword.NewPasswordViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Representa la pantalla de nueva contraseña.
 */
class NewPasswordScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<NewPasswordViewModel>()
        val state = viewModel.state.value

        // Diálogo para informar del éxito de la actualización
        InformationDialog(
            isVisible = state.success,
            title = stringResource(Res.string.attention),
            message = stringResource(Res.string.password_updated_success),
            buttonText = stringResource(Res.string.accept),
            onDismiss = {
                viewModel.clearSuccess()
                navigator.replaceAll(LoginScreen())
            }
        )

        if (state.error != null) {
            ErrorDialog(state.error) {
                viewModel.clearError()
            }
        }

        NewPasswordContent(
            state = state,
            onPasswordChange = viewModel::onPasswordChange,
            onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
            onUpdatePasswordClick = viewModel::onUpdatePasswordClick,
            onBackClick = navigator::pop
        )
    }
}