package es.ubu.bikeparkingapp.presentation.feature.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.do_not_have_an_account
import bikeparkingapp.composeapp.generated.resources.email
import bikeparkingapp.composeapp.generated.resources.forgot_password
import bikeparkingapp.composeapp.generated.resources.login
import bikeparkingapp.composeapp.generated.resources.password
import bikeparkingapp.composeapp.generated.resources.welcome
import es.ubu.bikeparkingapp.domain.model.AuthState
import es.ubu.bikeparkingapp.presentation.common.ext.handCursor
import org.jetbrains.compose.resources.stringResource

/**
 * Representa el contenido de la pantalla de inicio de sesión.
 * @property state Estado actual de la pantalla.
 * @property authState Estado de autenticación.
 * @property onEmailChange Función para manejar cambios en el campo de correo electrónico.
 * @property onPasswordChange Función para manejar cambios en el campo de contraseña.
 * @property onLoginClick Función para manejar el evento de inicio de sesión
 * @property onRegisterClick Función para manejar el evento de registro.
 * @property onPasswordResetClick Función para manejar el evento de restablecimiento de contraseña.
 */
@Composable
fun LoginContent(
    state: LoginState,
    authState: AuthState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onPasswordResetClick: () -> Unit
) {

    if (authState == AuthState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = stringResource(Res.string.welcome), fontSize = 32.sp)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = onEmailChange,
                label = { Text(stringResource(Res.string.email)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = { Text(stringResource(Res.string.password)) },
                modifier = Modifier.fillMaxWidth(),
                // Para ocultar la contraseña
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                // Ejecuta la acción de login al presionar enter desde el campo de contraseña
                keyboardActions = KeyboardActions(
                    onDone = {
                        onLoginClick()
                    }
                )
            )

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth().height(50.dp).handCursor()
            ) {
                Text(stringResource(Res.string.login))
            }

            TextButton(
                onClick = onRegisterClick,
                modifier = Modifier.handCursor()
            ) {
                Text(stringResource(Res.string.do_not_have_an_account))
            }
            TextButton(
                onClick = onPasswordResetClick,
                modifier = Modifier.handCursor()
            ) {
                Text(stringResource(Res.string.forgot_password))
            }
        }
    }
}