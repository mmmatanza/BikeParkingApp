package es.ubu.bikeparkingapp.presentation.feature.login

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import es.ubu.bikeparkingapp.domain.model.AuthState
import es.ubu.bikeparkingapp.presentation.feature.main.MainScreen
import es.ubu.bikeparkingapp.presentation.feature.passwordreset.PasswordResetScreen
import es.ubu.bikeparkingapp.presentation.feature.register.RegisterScreen
import es.ubu.bikeparkingapp.presentation.theme.AppTheme
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
        LaunchedEffect(viewModel.authState){
            viewModel.authState.collect { authState ->
                if(authState==AuthState.Authenticated)
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
                    Text(state.error)
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

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AppTheme {
        LoginScreen()
    }
}