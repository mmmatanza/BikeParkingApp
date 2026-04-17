package es.ubu.bikeparkingapp.presentation.common.components.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.no
import bikeparkingapp.composeapp.generated.resources.yes
import es.ubu.bikeparkingapp.presentation.common.ext.handCursor
import org.jetbrains.compose.resources.stringResource

/**
 * Muestra un diálogo de confirmación.
 */
@Composable
fun ConfirmationDialog(
    isVisible: Boolean,
    title: String,
    message: String,
    confirmText: String = stringResource(Res.string.yes),
    dismissText: String = stringResource(Res.string.no),
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = title, style = MaterialTheme.typography.headlineSmall)
            },
            text = {
                Text(text = message, style = MaterialTheme.typography.bodyMedium)
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm()
                        onDismiss() // Normalmente cerramos al confirmar
                    },
                    modifier = Modifier.handCursor()
                ) {
                    Text(confirmText)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.handCursor()
                ) {
                    Text(dismissText)
                }
            }
        )
    }
}