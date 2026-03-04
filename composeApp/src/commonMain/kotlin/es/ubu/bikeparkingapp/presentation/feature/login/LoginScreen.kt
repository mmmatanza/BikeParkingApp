package es.ubu.bikeparkingapp.presentation.feature.login

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
        val state = viewModel.state.value
        var showDialog by remember { mutableStateOf(false) }

        LaunchedEffect(state.isLoggedIn) {
            if(state.isLoggedIn)
                navigator.replace(MainScreen())
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
                    Text(state.error ?: "Error desconocido")
                }
            )
        }

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