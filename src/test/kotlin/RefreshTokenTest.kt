package xyz.mitzie

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import xyz.mitzie.dto.LoginUserRequest
import xyz.mitzie.dto.RegisterUserRequest
import xyz.mitzie.dto.TokenResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import xyz.mitzie.dto.LoginResult
import xyz.mitzie.dto.RefreshTokenRequest

class RefreshTokenTest  {
    private val route = "/refresh"

    suspend fun registerUser(client: HttpClient) {
        val registerBody = RegisterUserRequest(validUsername, validEmail, validPassword)
        client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(registerBody)
        }
    }

    suspend fun loginUser(client: HttpClient): TokenResponse {
        val loginBody = LoginUserRequest(validUsername, validPassword)
        val response = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(loginBody)
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val loginResult = response.body<LoginResult.Success>()
        return loginResult.tokens
    }

    @Test
    fun refreshRouteTest() = testApplication {
        environment { config = testConfig }
        application { module() }

        val client = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        // Register user first
        registerUser(client)
        val tokens = loginUser(client)

        val refreshTokenRequest = RefreshTokenRequest(tokens.refreshToken)

        client.post(route) {
            contentType(ContentType.Application.Json)
            setBody(refreshTokenRequest)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}

