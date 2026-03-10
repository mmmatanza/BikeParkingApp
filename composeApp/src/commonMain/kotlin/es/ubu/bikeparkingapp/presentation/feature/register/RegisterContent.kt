package es.ubu.bikeparkingapp.presentation.feature.register

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
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.administrator
import bikeparkingapp.composeapp.generated.resources.back
import bikeparkingapp.composeapp.generated.resources.email
import bikeparkingapp.composeapp.generated.resources.name
import bikeparkingapp.composeapp.generated.resources.password
import bikeparkingapp.composeapp.generated.resources.password_confirmation
import bikeparkingapp.composeapp.generated.resources.register
import bikeparkingapp.composeapp.generated.resources.role
import bikeparkingapp.composeapp.generated.resources.tax_id
import bikeparkingapp.composeapp.generated.resources.user
import es.ubu.bikeparkingapp.domain.entity.Role
import org.jetbrains.compose.resources.stringResource

/**
 * Representa el contenido de la pantalla de registro.
 *
 * @property state Estado actual de la pantalla.
 * @property onEmailChange Función para manejar cambios en el campo de correo electrónico.
 * @property onPasswordChange Función para manejar cambios en el campo de contraseña.
 * @property onPasswordConfirmationChange Función para manejar cambios en el campo de confirmación de contraseña.
 * @property onNameChange Función para manejar cambios en el campo de nombre.
 * @property onTaxIdChange Función para manejar cambios en el campo de DNI/NIF/CIF.
 * @property onRoleChange Función para manejar cambios en el campo de rol.
 * @property onRegisterClick Función para manejar el evento de registro.
 * @property onBackClick Función para manejar el evento de retroceso.
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterContent(
    state: RegisterState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordConfirmationChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onTaxIdChange: (String) -> Unit,
    onRoleChange: (Role) -> Unit,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit
){
    var roleExpanded by remember { mutableStateOf(false) }

    // Este Box permite centrarlo en la versión web
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
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.passwordConfirmation,
                onValueChange = onPasswordConfirmationChange,
                label = { Text(stringResource(Res.string.password_confirmation)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.name,
                onValueChange = onNameChange,
                label = { Text(stringResource(Res.string.name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.taxId,
                onValueChange = onTaxIdChange,
                label = { Text(stringResource(Res.string.tax_id)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = roleExpanded,
                onExpandedChange = { roleExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = when (state.role) {
                        Role.ADMIN -> stringResource(Res.string.administrator)
                        Role.USER -> stringResource(Res.string.user)
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(Res.string.role)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = roleExpanded,
                    onDismissRequest = { roleExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.user)) },
                        onClick = {
                            onRoleChange(Role.USER)
                            roleExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.administrator)) },
                        onClick = {
                            onRoleChange(Role.ADMIN)
                            roleExpanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRegisterClick,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(stringResource(Res.string.register))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(stringResource(Res.string.back))
            }

        }
    }
}