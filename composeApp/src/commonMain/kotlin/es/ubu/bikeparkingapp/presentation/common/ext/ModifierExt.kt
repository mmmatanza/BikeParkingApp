package es.ubu.bikeparkingapp.presentation.common.ext

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon

/**
 * Cambia el cursor al icono de la mano (Hand) cuando el ratón pasa por encima.
 * Útil para botones o elementos clickeables en Desktop/Web.
 */
fun Modifier.handCursor(): Modifier = this.pointerHoverIcon(PointerIcon.Hand)