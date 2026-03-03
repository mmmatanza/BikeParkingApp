package es.ubu.bikeparkingapp.presentation.feature.main

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _state = mutableStateOf(MainState())
    val state: State<MainState> = _state

}