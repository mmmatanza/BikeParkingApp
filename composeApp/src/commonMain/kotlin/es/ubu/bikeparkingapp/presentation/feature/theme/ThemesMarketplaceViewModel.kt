package es.ubu.bikeparkingapp.presentation.feature.theme

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.usecase.theme.ApplyThemeUseCase
import es.ubu.bikeparkingapp.domain.usecase.theme.GetThemesMarketplaceUseCase
import es.ubu.bikeparkingapp.domain.usecase.theme.RedeemPointsUseCase
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserIdUseCase
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserPointsUseCase
import es.ubu.bikeparkingapp.presentation.common.util.ErrorMapper
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla del mercado de temas.
 *
 * @property getThemesMarketplaceUseCase Caso de uso para obtener los temas.
 * @property redeemPointsUseCase Caso de uso para canjear puntos por un tema.
 * @property applyThemeUseCase Caso de uso para aplicar un tema.
 * @property getUserPointsUseCase Caso de uso para obtener los puntos del usuario.
 * @property getUserIdUseCase Caso de uso para obtener el ID del usuario actual.
 */
class ThemesMarketplaceViewModel(
    private val getThemesMarketplaceUseCase: GetThemesMarketplaceUseCase,
    private val redeemPointsUseCase: RedeemPointsUseCase,
    private val applyThemeUseCase: ApplyThemeUseCase,
    private val getUserPointsUseCase: GetUserPointsUseCase,
    private val getUserIdUseCase: GetUserIdUseCase
) : ViewModel() {

    private val _state = mutableStateOf(ThemesMarketplaceState())
    val state: State<ThemesMarketplaceState> = _state

    init {
        loadMarketplace()
    }

    fun loadMarketplace() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            getUserIdUseCase().onSuccess { accountId ->
                val themesResult = getThemesMarketplaceUseCase.invoke(accountId)
                val pointsResult = getUserPointsUseCase.invoke(accountId)

                if (themesResult.isSuccess && pointsResult.isSuccess) {
                    _state.value = _state.value.copy(
                        themes = themesResult.getOrDefault(emptyList()),
                        userPoints = pointsResult.getOrDefault(0),
                        isLoading = false
                    )
                } else {
                    val exception = (themesResult.exceptionOrNull() ?: pointsResult.exceptionOrNull()) as? Exception
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = exception
                    )
                }
            }.onFailure {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = it as? Exception
                )
            }
        }
    }

    fun redeemTheme(themeId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            getUserIdUseCase().onSuccess { accountId ->
                redeemPointsUseCase.invoke(accountId, themeId).onSuccess {
                    loadMarketplace()
                }.onFailure {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = ErrorMapper.map(it)
                    )
                }
            }
        }
    }

    fun applyTheme(themeId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            getUserIdUseCase().onSuccess { accountId ->
                applyThemeUseCase.invoke(accountId, themeId).onSuccess {
                    loadMarketplace()
                }.onFailure {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = ErrorMapper.map(it)
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
