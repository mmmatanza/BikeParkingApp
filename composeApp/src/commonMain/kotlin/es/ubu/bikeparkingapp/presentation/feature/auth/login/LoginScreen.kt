package es.ubu.bikeparkingapp.presentation.feature.auth.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import es.ubu.bikeparkingapp.domain.model.AuthState
import es.ubu.bikeparkingapp.presentation.common.components.dialog.ErrorDialog
import es.ubu.bikeparkingapp.presentation.feature.auth.passwordreset.PasswordResetScreen
import es.ubu.bikeparkingapp.presentation.feature.auth.register.RegisterScreen
import es.ubu.bikeparkingapp.presentation.feature.main.MainScreen
import org.koin.compose.viewmodel.koinViewModel

/**
 * Representa la pantalla de inicio de sesión.
 */
class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<LoginViewModel>()
        val state = viewModel.state.value

        // Se observa el estado de autenticación del ViewModel
        LaunchedEffect(state.loginSuccess) {
            if (state.loginSuccess) {
                // Para web es necesario limpiar el estado de loginSuccess
                viewModel.onNavigatedToMain()
                navigator.replaceAll(MainScreen())
            }
        }

        if (state.error != null) {
            ErrorDialog(state.error) {
                viewModel.clearError()
            }
        }

        LoginContent(
            state = viewModel.state.value,
            authState = viewModel.authState.collectAsState(AuthState.Loading).value,
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onLoginClick = viewModel::onLoginClick,
            onRegisterClick = {
                navigator.push(RegisterScreen())
            },
            onPasswordResetClick = {
                navigator.push(PasswordResetScreen())
            }
        )
    }
}