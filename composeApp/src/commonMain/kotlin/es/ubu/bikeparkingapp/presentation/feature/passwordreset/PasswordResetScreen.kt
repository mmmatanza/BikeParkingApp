package es.ubu.bikeparkingapp.presentation.feature.passwordreset

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.accept
import bikeparkingapp.composeapp.generated.resources.attention
import bikeparkingapp.composeapp.generated.resources.error
import bikeparkingapp.composeapp.generated.resources.no_internet
import bikeparkingapp.composeapp.generated.resources.reset_advice
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import org.jetbrains.compose.resources.stringResource
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
                        Text(stringResource(Res.string.accept))
                    }
                },
                title = {
                    Text(stringResource(Res.string.attention))
                },
                text = {
                    Text(stringResource(Res.string.reset_advice))
                }
            )
        }

        if (state.error != null) {
            AlertDialog(
                onDismissRequest = { viewModel.clearError() },
                confirmButton = {
                    Button(onClick = { viewModel.clearError() }) {
                        Text(stringResource(Res.string.accept))
                    }
                },
                title = {
                    Text(stringResource(Res.string.error))
                },
                text = {
                    when(state.error){
                        is NoNetworkException -> Text(stringResource(Res.string.no_internet))
                        else -> Text(stringResource(Res.string.error))
                    }
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