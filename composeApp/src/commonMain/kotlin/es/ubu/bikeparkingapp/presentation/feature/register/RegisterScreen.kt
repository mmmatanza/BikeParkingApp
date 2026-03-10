package es.ubu.bikeparkingapp.presentation.feature.register

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.accept
import bikeparkingapp.composeapp.generated.resources.email_invalid
import bikeparkingapp.composeapp.generated.resources.error
import bikeparkingapp.composeapp.generated.resources.name_empty
import bikeparkingapp.composeapp.generated.resources.password_mismatch
import bikeparkingapp.composeapp.generated.resources.tax_id_empty
import bikeparkingapp.composeapp.generated.resources.weak_password
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import es.ubu.bikeparkingapp.domain.exception.EmailInvalidException
import es.ubu.bikeparkingapp.domain.exception.RegisterException
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Representa la pantalla de registro.
 *
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
                    when (state.error) {
                        is RegisterException.NameEmptyException -> Text(stringResource(Res.string.name_empty))
                        is EmailInvalidException -> Text(stringResource(Res.string.email_invalid))
                        is RegisterException.TaxIdEmptyException -> Text(stringResource(Res.string.tax_id_empty))
                        is RegisterException.PasswordMismatchException -> Text(stringResource(Res.string.password_mismatch))
                        is RegisterException.WeakPasswordException -> Text(stringResource(Res.string.weak_password))
                        else -> Text(stringResource(Res.string.error))
                    }
                }
            )
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