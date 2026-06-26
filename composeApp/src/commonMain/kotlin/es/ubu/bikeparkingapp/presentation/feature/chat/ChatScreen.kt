package es.ubu.bikeparkingapp.presentation.feature.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import es.ubu.bikeparkingapp.presentation.common.components.dialog.ErrorDialog
import org.koin.compose.viewmodel.koinViewModel

/**
 * Pantalla de chat inteligente para el usuario.
 */
class ChatScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<ChatViewModel>()
        val state = viewModel.state.value

        LaunchedEffect(Unit) {
            viewModel.loadChatHistory()
        }

        if (state.error != null) {
            ErrorDialog(state.error) {
                viewModel.clearError()
            }
        }

        ChatContent(
            state = state,
            actions = ChatActions(
                onBackClick = {
                    viewModel.clearState()
                    navigator.pop()
                },
                onInputChange = viewModel::onInputChange,
                onSendMessage = viewModel::sendMessage
            )
        )
    }
}
