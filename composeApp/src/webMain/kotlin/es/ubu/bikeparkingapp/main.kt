package es.ubu.bikeparkingapp

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import es.ubu.bikeparkingapp.di.initKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    js("require('@js-joda/timezone')")
    initKoin()
    ComposeViewport {
        App()
    }
}