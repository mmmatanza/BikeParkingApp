package es.ubu.bikeparkingapp.presentation.common.components.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import bikeparkingapp.composeapp.generated.resources.Res
import bikeparkingapp.composeapp.generated.resources.accept
import bikeparkingapp.composeapp.generated.resources.error
import es.ubu.bikeparkingapp.presentation.common.ext.handCursor
import es.ubu.bikeparkingapp.presentation.common.ext.toUserMessage
import org.jetbrains.compose.resources.stringResource

/**
 * Muestra un diálogo de error con el mensaje del error.
 */
@Composable
fun ErrorDialog(
    error: Throwable?,
    onDismiss: () -> Unit
) {
    if (error == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss, modifier = Modifier.handCursor()) {
                Text(stringResource(Res.string.accept))
            }
        },
        title = {
            Text(stringResource(Res.string.error))
        },
        text = {
            Text(text = error.toUserMessage())
        }
    )
}