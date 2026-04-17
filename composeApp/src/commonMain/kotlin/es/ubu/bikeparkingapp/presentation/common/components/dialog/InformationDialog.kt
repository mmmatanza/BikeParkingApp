package es.ubu.bikeparkingapp.presentation.common.components.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.accept
import es.ubu.bikeparkingapp.presentation.common.ext.handCursor
import org.jetbrains.compose.resources.stringResource

/**
 * Muestra un diálogo de información.
 */
@Composable
fun InformationDialog(
    isVisible: Boolean,
    title: String,
    message: String,
    buttonText: String = stringResource(Res.string.accept),
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
                    onClick = onDismiss,
                    modifier = Modifier.handCursor()
                ) {
                    Text(buttonText)
                }
            }
        )
    }
}