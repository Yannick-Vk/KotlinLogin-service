package xyz.mitzie

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.coroutines.delay
import xyz.mitzie.dto.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation

class RefreshTokenTest {
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
    fun successfulTokenRefresh() = testApplication {
        environment { config = testConfig }
        application { module() }

        val client = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        // Register user first
        registerUser(client)
        val initialTokens = loginUser(client)

        // Add a small delay to ensure 'iat' and 'exp' claims are different
        delay(10L)

        val refreshTokenRequest = RefreshTokenRequest(initialTokens.refreshToken)

        val response = client.post(route) {
            contentType(ContentType.Application.Json)
            setBody(refreshTokenRequest)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }

        val refreshResult = response.body<RefreshTokenResult.Success>()
        val newTokens = refreshResult.tokens

        assertNotNull(newTokens.refreshToken, "Refresh-Token was null!")
        assertNotNull(newTokens.accessToken, "Access-Token was null!")

        assertNotEquals(
            initialTokens.accessToken,
            newTokens.accessToken,
            "New access token should be different from the initial one! Old vs New: ${initialTokens.accessToken} == ${newTokens.accessToken}"
        )
        assertNotEquals(
            initialTokens.refreshToken,
            newTokens.refreshToken,
            "New refresh token should be different from the initial one! Old vs New: ${initialTokens.refreshToken} == ${newTokens.refreshToken}"
        )

        client.get("/user") {
            header(HttpHeaders.Authorization, "Bearer ${newTokens.accessToken}")
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status)
        }
    }


    @Test
    fun testInvalidTokenRefresh() = testApplication {
        environment { config = testConfig }
        application { module() }

        val client = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        val request = RefreshTokenRequest(refreshToken = "invalid")

        client.post(route) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.apply {
            assertEquals(HttpStatusCode.Unauthorized, status)
            // The actual response body will be a plain string, not a JSON object
            assertEquals("Invalid or expired refresh token.", bodyAsText())
        }
    }
}