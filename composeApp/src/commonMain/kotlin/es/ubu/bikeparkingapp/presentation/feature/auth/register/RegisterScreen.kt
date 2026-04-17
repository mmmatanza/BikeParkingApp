package es.ubu.bikeparkingapp.presentation.feature.auth.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import es.ubu.bikeparkingapp.presentation.common.components.dialog.ErrorDialog
import org.koin.compose.viewmodel.koinViewModel

/**
 * Representa la pantalla de registro.
 */
class RegisterScreen: Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<RegisterViewModel>()
        val state = viewModel.state.value

        LaunchedEffect(state.isSuccess) {
            if (state.isSuccess) navigator.pop()
        }

        if (state.error != null) {
            ErrorDialog(state.error) {
                viewModel.clearError()
            }
        }

        RegisterContent(
            state,
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onPasswordConfirmationChange = viewModel::onPasswordConfirmationChange,
            onNameChange = viewModel::onNameChange,
            onTaxIdChange = viewModel::onTaxIdChange,
            onRoleChange = viewModel::onRoleChange,
            onRegisterClick = viewModel::onRegisterClick,
            onBackClick = navigator::pop
        )
    }
}