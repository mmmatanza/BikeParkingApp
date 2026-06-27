package es.ubu.bikeparkingapp.presentation.feature.chat

import es.ubu.bikeparkingapp.domain.usecase.chat.GetChatHistoryUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.chat.SendChatMessageUseCaseImpl
import es.ubu.bikeparkingapp.helper.repositories.FakeChatRepository
import es.ubu.bikeparkingapp.helper.usecases.user.FakeGetUserIdUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeChatRepo: FakeChatRepository
    private lateinit var viewModel: ChatViewModel
    private val accountId = "test_user"

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeChatRepo = FakeChatRepository()
        val sendUseCase = SendChatMessageUseCaseImpl(fakeChatRepo)
        val getHistoryUseCase = GetChatHistoryUseCaseImpl(fakeChatRepo)
        val getUserIdUseCase = FakeGetUserIdUseCase().apply { response = accountId }

        viewModel = ChatViewModel(getHistoryUseCase, sendUseCase, getUserIdUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Al cargar el ViewModel se puede obtener el historial`() = runTest {
        viewModel.loadChatHistory()
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.state.value.isLoading)
        assertTrue(viewModel.state.value.messages.isEmpty())
    }

    @Test
    fun `Enviar un mensaje actualiza el estado correctamente`() = runTest {
        val message = "Hi Chat!"
        viewModel.onInputChange(message)
        viewModel.sendMessage()
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Mensaje del usuario + respuesta del llm
        assertEquals(2, viewModel.state.value.messages.size)
        assertEquals("", viewModel.state.value.currentInput)
        assertFalse(viewModel.state.value.isLoading)
    }
}
