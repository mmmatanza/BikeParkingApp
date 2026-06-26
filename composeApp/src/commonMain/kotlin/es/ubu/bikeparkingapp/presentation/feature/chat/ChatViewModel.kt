package es.ubu.bikeparkingapp.presentation.feature.chat

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.entity.ChatMessage
import es.ubu.bikeparkingapp.domain.entity.MessageRole
import es.ubu.bikeparkingapp.domain.usecase.chat.GetChatHistoryUseCase
import es.ubu.bikeparkingapp.domain.usecase.chat.SendChatMessageUseCase
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserIdUseCase
import es.ubu.bikeparkingapp.presentation.common.util.ErrorMapper
import kotlinx.coroutines.launch
import kotlin.time.Clock

/**
 * Representación del ViewModel de la pantalla de chat.
 * @property getChatHistoryUseCase Caso de uso para obtener el historial del chat.
 * @property sendChatMessageUseCase Caso de uso para enviar un mensaje al chat.
 * @property getUserIdUseCase Caso de uso para obtener el ID del usuario.
 */
class ChatViewModel(
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val getUserIdUseCase: GetUserIdUseCase
) : ViewModel() {

    private val _state = mutableStateOf(ChatState())
    val state: State<ChatState> = _state

    fun loadChatHistory() {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            getUserIdUseCase()
                .onSuccess { accountId ->
                    getChatHistoryUseCase(accountId)
                        .onSuccess { history ->
                            _state.value = _state.value.copy(
                                messages = history,
                                isLoading = false,
                                error = null
                            )
                        }
                        .onFailure { e ->
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = ErrorMapper.map(e)
                            )
                        }
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = ErrorMapper.map(e)
                    )
                }
        }
    }

    fun onInputChange(newInput: String) {
        _state.value = _state.value.copy(currentInput = newInput)
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun clearState() {
        _state.value = ChatState()
    }

    fun sendMessage() {
        val content = _state.value.currentInput
        if (content.isBlank()) return

        // Añadimos el mensaje del usuario localmente de forma inmediata
        val userMessage = ChatMessage(
            accountId = "", // No importa para la UI local
            role = MessageRole.USER,
            content = content,
            createdAt = Clock.System.now()
        )

        _state.value = _state.value.copy(
            messages = _state.value.messages + userMessage,
            currentInput = "",
            isLoading = true
        )
        
        viewModelScope.launch {
            getUserIdUseCase()
                .onSuccess { accountId ->
                    sendChatMessageUseCase(accountId, content)
                        .onSuccess { assistantMessage ->
                            val updatedMessages = _state.value.messages + assistantMessage
                            _state.value = _state.value.copy(
                                messages = updatedMessages,
                                isLoading = false,
                                error = null
                            )
                        }
                        .onFailure { e ->
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = ErrorMapper.map(e)
                            )
                        }
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = ErrorMapper.map(e)
                    )
                }
        }
    }
}
