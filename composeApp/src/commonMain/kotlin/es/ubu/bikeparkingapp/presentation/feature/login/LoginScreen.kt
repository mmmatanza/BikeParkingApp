package es.ubu.bikeparkingapp.presentation.feature.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import es.ubu.bikeparkingapp.presentation.feature.main.MainScreen
import es.ubu.bikeparkingapp.presentation.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<LoginViewModel>()

        LoginContent(
            state = viewModel.state.value,
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onLoginClick = viewModel::onLoginClick,
            onLoginSuccess = {
                navigator.replace(MainScreen())
            },
            onRegisterClick = {
                //navigator.push(RegisterScreen())
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