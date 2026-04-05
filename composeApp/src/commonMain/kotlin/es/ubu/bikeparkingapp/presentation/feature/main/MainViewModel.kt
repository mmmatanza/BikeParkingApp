package es.ubu.bikeparkingapp.presentation.feature.main

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import es.ubu.bikeparkingapp.domain.model.AuthState
import es.ubu.bikeparkingapp.domain.usecase.auth.GetAuthStateUseCase
import es.ubu.bikeparkingapp.domain.usecase.auth.SignoutUseCase
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserRoleUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.launch

/**
 * Representa el viewModel para la pantalla principal.
 *
 * @property signoutUseCase Caso de uso para cerrar sesión.
 * @property getAuthStateUseCase Caso de uso para obtener el estado de autenticación.
 */
class MainViewModel(
    private val signoutUseCase: SignoutUseCase,
    private val getAuthStateUseCase: GetAuthStateUseCase,
    private val getUserRoleUseCase: GetUserRoleUseCase
) : ViewModel() {

    private val _state = mutableStateOf(MainState())
    val state: State<MainState> = _state
    val authState: Flow<AuthState> = getAuthStateUseCase()
        .dropWhile { it == AuthState.Unauthenticated }

    init {
        observeAuthAndLoadRole()
    }

    private fun observeAuthAndLoadRole() {
        viewModelScope.launch {
            authState.collect { auth ->
                when (auth) {
                    is AuthState.Authenticated -> fetchRole()
                    is AuthState.Unauthenticated -> _state.value = MainState()
                    else -> {}
                }
            }
        }
    }

    private fun fetchRole() {
        viewModelScope.launch {
            var lastError: Throwable? = null

            repeat(5) { attempt -> // Más reintentos, la caché puede tardar
                val result = getUserRoleUseCase()
                if (result.isSuccess) {
                    _state.value = _state.value.copy(
                        userRole = result.getOrNull(),
                        error = null
                    )
                    return@launch
                }
                lastError = result.exceptionOrNull()
                delay(300L * (attempt + 1)) // 300ms, 600ms, 900ms, 1200ms, 1500ms
            }

            _state.value = _state.value.copy(
                error = lastError?.let {
                    if (it is Exception) it else Exception(it.message)
                }
            )
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun onSignoutClick() {
        viewModelScope.launch {
            signoutUseCase().onFailure { error ->
                when (error) {
                    is NoNetworkException -> _state.value = _state.value.copy(error = error)
                    else -> _state.value = _state.value.copy(error = Exception(error.message))
                }

            }
        }
    }


}