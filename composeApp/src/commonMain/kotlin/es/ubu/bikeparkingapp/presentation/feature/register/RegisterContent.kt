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
import es.ubu.bikeparkingapp.domain.entity.Role

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

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.passwordConfirmation,
                onValueChange = onPasswordConfirmationChange,
                label = { Text("Repetir contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.name,
                onValueChange = onNameChange,
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.taxId,
                onValueChange = onTaxIdChange,
                label = { Text("DNI/NIF/CIF") },
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
                        Role.ADMIN -> "Administrador"
                        Role.USER -> "Usuario"
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Rol") },
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
                        text = { Text("Usuario") },
                        onClick = {
                            onRoleChange(Role.USER)
                            roleExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Administrador") },
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
                Text("Registrarse")
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