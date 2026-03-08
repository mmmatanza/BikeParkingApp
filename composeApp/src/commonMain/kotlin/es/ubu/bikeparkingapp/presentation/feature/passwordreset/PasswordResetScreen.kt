package es.ubu.bikeparkingapp.presentation.feature.passwordreset

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel

/**
 * Representa la pantalla de restablecer contraseña.
 *
 */
class PasswordResetScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<PasswordResetViewModel>()
        val state = viewModel.state.value

        if(state.success){
            AlertDialog(
                onDismissRequest = { viewModel.clearSuccess() },
                confirmButton = {
                    Button(onClick = { viewModel.clearSuccess() }) {
                        Text("Aceptar")
                    }
                },
                title = {
                    Text("Aviso")
                },
                text = {
                    Text("Se ha enviado un correo electrónico de restablecimiento a la dirección indicada.")
                }
            )
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
                    Text("Aviso")
                },
                text = {
                    Text(state.error)
                }
            )
        }

        PasswordResetContent(
            state,
            onPasswordResetClick = viewModel::onPasswordResetClick,
            onEmailChange = viewModel::onEmailChange,
            onBackClick = navigator::pop
        )
    }
}