package es.ubu.bikeparkingapp.presentation.feature.main

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.model.AuthState
import es.ubu.bikeparkingapp.domain.usecase.auth.GetAuthStateUseCase
import es.ubu.bikeparkingapp.domain.usecase.auth.SignoutUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(
    private val signoutUseCase: SignoutUseCase,
    getAuthStateUseCase: GetAuthStateUseCase
) : ViewModel() {

    private val _state = mutableStateOf(MainState())
    val state: State<MainState> = _state

    val authState: Flow<AuthState> = getAuthStateUseCase()

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun onSignoutClick() {
        viewModelScope.launch {
            signoutUseCase()
                .onFailure { error ->
                    _state.value = _state.value.copy(error = error.message)
                }
        }
    }

}