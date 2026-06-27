package es.ubu.bikeparkingapp.data.repository

import es.ubu.bikeparkingapp.data.dto.ChatMessageDto
import es.ubu.bikeparkingapp.data.mapper.ErrorMapper
import es.ubu.bikeparkingapp.data.mapper.toDomain
import es.ubu.bikeparkingapp.domain.entity.ChatMessage
import es.ubu.bikeparkingapp.domain.entity.MessageRole
import es.ubu.bikeparkingapp.domain.exception.ChatServiceUnavailableException
import es.ubu.bikeparkingapp.domain.exception.NoActiveSessionException
import es.ubu.bikeparkingapp.domain.repository.ChatRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlin.time.Clock

/**
 * Implementación del repositorio de chat utilizando Supabase y el servicio ML.
 */
class SupabaseChatRepository(
    private val supabaseClient: SupabaseClient,
    private val httpClient: HttpClient,
    private val baseUrl: String = "http://192.168.1.25:8000"
) : ChatRepository {

    override suspend fun getChatHistory(accountId: String): Result<List<ChatMessage>> = runCatching {
        supabaseClient.from("chat_messages")
            .select {
                filter {
                    eq("account_id", accountId)
                }
                order("created_at", order = Order.ASCENDING)
            }
            .decodeList<ChatMessageDto>()
            .map { it.toDomain() }
    }.onFailure { throw ErrorMapper.map(it) }

    override suspend fun sendMessage(accountId: String, content: String): Result<ChatMessage> = runCatching {
        val token = supabaseClient.auth.currentAccessTokenOrNull()
            ?: throw NoActiveSessionException()

        // Si falla, se lanza una excepción y no se inserta nada en la base de datos.
        val assistantContent = try {
            val response: HttpResponse = httpClient.post("$baseUrl/chat") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "message" to content,
                    "account_id" to accountId
                ))
            }
            
            when (response.status) {
                HttpStatusCode.OK -> {
                    val body = response.body<Map<String, String>>()
                    body["response"] ?: throw ChatServiceUnavailableException()
                }
                HttpStatusCode.Unauthorized -> throw NoActiveSessionException()
                else -> throw ChatServiceUnavailableException()
            }
        } catch (e: NoActiveSessionException) {
            throw e
        } catch (e: Exception) {
            throw ErrorMapper.map(e)
        }

        val now = Clock.System.now().toString()

        // Insertamos el mensaje del usuario
        val userMessage = ChatMessageDto(
            accountId = accountId,
            role = MessageRole.USER.name,
            content = content,
            createdAt = now
        )
        supabaseClient.from("chat_messages").insert(userMessage)

        // Insertamos la respuesta del asistente
        val assistantResponse = ChatMessageDto(
            accountId = accountId,
            role = MessageRole.ASSISTANT.name,
            content = assistantContent,
            createdAt = Clock.System.now().toString()
        )
        supabaseClient.from("chat_messages").insert(assistantResponse)
        
        assistantResponse.toDomain()
    }.onFailure { throw ErrorMapper.map(it) }
}
