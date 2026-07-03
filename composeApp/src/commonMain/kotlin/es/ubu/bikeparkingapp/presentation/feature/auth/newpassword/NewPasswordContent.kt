package es.ubu.bikeparkingapp.presentation.feature.auth.newpassword

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.back
import bikeparkingapp.composeapp.generated.resources.new_password
import bikeparkingapp.composeapp.generated.resources.password
import bikeparkingapp.composeapp.generated.resources.password_confirmation
import bikeparkingapp.composeapp.generated.resources.update_password
import es.ubu.bikeparkingapp.presentation.common.ext.handCursor
import org.jetbrains.compose.resources.stringResource

/**
 * Representa el contenido de la pantalla de nueva contraseña.
 * @property state Estado actual de la pantalla.
 * @property onPasswordChange Función para manejar cambios en la contraseña.
 * @property onConfirmPasswordChange Función para manejar cambios en la confirmación de la contraseña.
 * @property onUpdatePasswordClick Función para manejar el evento de actualizar contraseña.
 * @property onBackClick Función para manejar el evento de retroceso.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPasswordContent(
    state: NewPasswordState,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onUpdatePasswordClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.new_password)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.handCursor()) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
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

                OutlinedTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    label = { Text(stringResource(Res.string.password)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = { Text(stringResource(Res.string.password_confirmation)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(36.dp))

                Button(
                    onClick = onUpdatePasswordClick,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth().height(50.dp).handCursor()
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Text(stringResource(Res.string.update_password))
                    }
                }
            }
        }
    }
}
