package es.ubu.bikeparkingapp.presentation.feature.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Representa el contenido de la pantalla de inicio de sesión.
 *
 * @property state Estado actual de la pantalla.
 * @property onEmailChange Función para manejar cambios en el campo de correo electrónico.
 * @property onPasswordChange Función para manejar cambios en el campo de contraseña.
 * @property onLoginClick Función para manejar el evento de inicio de sesión
 * @property onRegisterClick Función para manejar el evento de registro.
 * @property onPasswordResetClick Función para manejar el evento de restablecimiento de contraseña.
 */
@Composable
fun LoginContent(
    state: LoginState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onPasswordResetClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Bienvenido", fontSize = 32.sp)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = onEmailChange,
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Iniciar Sesión")
            }

            TextButton(onClick = onRegisterClick) {
                Text("¿No tienes cuenta? Regístrate")
            }
            TextButton(onClick = onPasswordResetClick) {
                Text("¿Olvidaste la contraseña? Restablécela")
            }
        }
    }
}