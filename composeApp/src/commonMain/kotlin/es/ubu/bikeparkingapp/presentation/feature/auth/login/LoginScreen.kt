package es.ubu.bikeparkingapp.presentation.feature.auth.login

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.email_invalid
import bikeparkingapp.composeapp.generated.resources.generic_error
import bikeparkingapp.composeapp.generated.resources.no_active_session
import bikeparkingapp.composeapp.generated.resources.no_internet
import bikeparkingapp.composeapp.generated.resources.password_empty
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import es.ubu.bikeparkingapp.domain.exception.EmailInvalidException
import es.ubu.bikeparkingapp.domain.exception.NoActiveSessionException
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import es.ubu.bikeparkingapp.domain.exception.PasswordEmptyException
import es.ubu.bikeparkingapp.domain.model.AuthState
import es.ubu.bikeparkingapp.presentation.feature.auth.passwordreset.PasswordResetScreen
import es.ubu.bikeparkingapp.presentation.feature.auth.register.RegisterScreen
import es.ubu.bikeparkingapp.presentation.feature.main.MainScreen
import org.jetbrains.compose.resources.stringResource
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
            AlertDialog(
                onDismissRequest = { viewModel.clearError() },
                confirmButton = {
                    Button(onClick = { viewModel.clearError() }) {
                        Text("Aceptar")
                    }
                },
                title = {
                    Text("Error")
                },
                text = {
                    when(state.error){
                        is EmailInvalidException -> Text(stringResource(Res.string.email_invalid))
                        is PasswordEmptyException -> Text(stringResource(Res.string.password_empty))
                        is NoNetworkException -> Text(stringResource(Res.string.no_internet))
                        is NoActiveSessionException -> Text(stringResource(Res.string.no_active_session))
                        else -> Text(stringResource(Res.string.generic_error))
                    }
                }
            )
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