package es.ubu.bikeparkingapp.presentation.feature.parking.mapselection

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel

/**
 * Representa la pantalla de selección de ubicación en el mapa.
 * @property onLocationSelected Función para manejar la selección de ubicación.
 */
class MapSelectionScreen(
    private val onLocationSelected: (Double?, Double?) -> Unit
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<MapSelectionViewModel>()
        val state = viewModel.state.value

        MapSelectionContent(
            state = state,
            onCoordinatesChange = viewModel::onCoordinatesChange,
            onLocationSelected = {
                viewModel.onConfirmSelection(onLocationSelected)
                navigator.pop()
            },
            onClearCoordinates = {
                viewModel.onClearCoordinates()
            },
            onBackClick = navigator::pop
        )

    }
}