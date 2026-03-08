package es.ubu.bikeparkingapp.presentation.feature.passwordreset

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Representa el contenido de la pantalla de restablecer contraseña.
 *
 * @property state Estado actual de la pantalla.
 * @property onEmailChange Función para manejar cambios en el campo de correo electrónico.
 * @property onPasswordResetClick Función para manejar el evento de restablecer contraseña.
 * @property onBackClick Función para manejar el evento de retroceso.
 *
 */
@Composable
fun PasswordResetContent(
    state: PasswordResetState,
    onPasswordResetClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    onBackClick: () -> Unit
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

            OutlinedTextField(
                value = state.email,
                onValueChange = onEmailChange,
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onPasswordResetClick,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Resetear Contraseña")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Atrás")
            }

        }
    }
}