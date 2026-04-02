package xyz.mitzie

import io.ktor.client.*
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
        client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(loginBody)
        }

        // return tokens
        return
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

        client.post(route) {
            contentType(ContentType.Application.Json)
            setBody(null)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}

