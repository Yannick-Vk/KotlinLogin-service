package xyz.mitzie

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import xyz.mitzie.dto.LoginUserRequest
import xyz.mitzie.dto.RegisterUserRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation

class LoginTest {
    // Requests
    private val emptyUsernameRequest = LoginUserRequest("", validPassword)
    private val emptyPasswordRequest = LoginUserRequest(validUsername, "")

    private val route = "/login"

    private var userRegistered = false

    suspend fun registerUser(client: HttpClient) {
        if (userRegistered) return

        val registerBody = RegisterUserRequest(validUsername, validEmail, validPassword)

        client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(registerBody)
        }

        println("registered user")

        userRegistered = true
    }

    @Test
    fun testRegisterWithEmptyUsername() = testApplication {
        environment { config = testConfig }
        application { module() }

        val client = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        client.post(route) {
            contentType(ContentType.Application.Json)
            setBody(emptyUsernameRequest)
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            assertEquals("Username cannot be empty", bodyAsText())
        }
    }

    @Test
    fun testRegisterWithEmptyPassword() = testApplication {
        environment { config = testConfig }
        application { module() }

        val client = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        client.post(route) {
            contentType(ContentType.Application.Json)
            setBody(emptyPasswordRequest)
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            assertEquals("Password cannot be empty", bodyAsText())
        }
    }


    @Test
    fun testLoginSuccess() = testApplication {
        environment { config = testConfig }
        application { module() }

        val client = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        registerUser(client)

        val loginBody = LoginUserRequest(validUsername, validPassword)

        client.post(route) {
            contentType(ContentType.Application.Json)
            setBody(loginBody)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }

    }

    @Test
    fun testLoginUserNotFound() = testApplication {
        environment { config = testConfig }
        application { module() }

        val client = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        registerUser(client)

        val loginBody = LoginUserRequest("unknown_username", "unknown password")

        client.post(route) {
            contentType(ContentType.Application.Json)
            setBody(loginBody)
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
        }

    }

    @Test
    fun testLoginWrongPassword() = testApplication {
        environment { config = testConfig }
        application { module() }

        val client = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        registerUser(client)

        val loginBody = LoginUserRequest(validUsername, "wrong password")

        client.post(route) {
            contentType(ContentType.Application.Json)
            setBody(loginBody)
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
        }

    }
}
