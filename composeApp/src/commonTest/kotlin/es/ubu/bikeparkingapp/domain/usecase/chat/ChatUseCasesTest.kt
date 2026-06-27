package es.ubu.bikeparkingapp.domain.usecase.chat

import es.ubu.bikeparkingapp.helper.repositories.FakeChatRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChatUseCasesTest {

    private lateinit var fakeChatRepo: FakeChatRepository
    private lateinit var sendChatMessageUseCase: SendChatMessageUseCaseImpl
    private lateinit var getChatHistoryUseCase: GetChatHistoryUseCaseImpl
    private val accountId = "test_user"

    @BeforeTest
    fun setUp() {
        fakeChatRepo = FakeChatRepository()
        sendChatMessageUseCase = SendChatMessageUseCaseImpl(fakeChatRepo)
        getChatHistoryUseCase = GetChatHistoryUseCaseImpl(fakeChatRepo)
    }

    @Test
    fun `Enviar un mensaje suma el mensaje y la respuesta al historial`() = runTest {
        val content = "Hi"
        val result = sendChatMessageUseCase(accountId, content)
        
        assertTrue(result.isSuccess)
        val historyResult = getChatHistoryUseCase(accountId)
        assertTrue(historyResult.isSuccess)
        assertEquals(2, historyResult.getOrNull()?.size)
    }

    @Test
    fun `Obtener historial retorna error si el repositorio falla`() = runTest {
        fakeChatRepo.shouldReturnError = true
        val result = getChatHistoryUseCase(accountId)
        assertTrue(result.isFailure)
    }
}
