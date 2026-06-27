package es.ubu.bikeparkingapp.integration

import es.ubu.bikeparkingapp.domain.usecase.chat.GetChatHistoryUseCaseImpl
import es.ubu.bikeparkingapp.domain.usecase.chat.SendChatMessageUseCaseImpl
import es.ubu.bikeparkingapp.helper.repositories.FakeChatRepository
import es.ubu.bikeparkingapp.helper.usecases.user.FakeGetUserIdUseCase
import es.ubu.bikeparkingapp.presentation.feature.chat.ChatViewModel
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
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class ChatIntegrationTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeChatRepo: FakeChatRepository
    private lateinit var viewModel: ChatViewModel
    private val accountId = "integration_test_user"

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
    fun `Flujo completo de chat funciona correctamente`() = runTest {
        // Cargar historial inicial
        viewModel.loadChatHistory()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, viewModel.state.value.messages.size)

        // Enviar mensaje
        val question = "Whats a red marker in the map?"
        viewModel.onInputChange(question)
        viewModel.sendMessage()
        
        testDispatcher.scheduler.advanceUntilIdle()

        // Verificar que hay dos mensajes: el enviado y la respuesta
        assertEquals(2, viewModel.state.value.messages.size)
        assertEquals(question, viewModel.state.value.messages[0].content)
        assertNotNull(viewModel.state.value.messages[1].content)
        
        // Verificar que el historial persistió en el repositorio fake
        val historyResult = fakeChatRepo.getChatHistory(accountId)
        assertEquals(2, historyResult.getOrNull()?.size)
    }
}
