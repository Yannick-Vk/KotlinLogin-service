package xyz.mitzie

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import xyz.mitzie.AuthTestHelper.registerUser
import xyz.mitzie.AuthTestHelper.validPassword
import xyz.mitzie.AuthTestHelper.validUsername
import xyz.mitzie.dto.LoginUserRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation

class LoginTest {
    // Requests
    private val emptyUsernameRequest = LoginUserRequest("", validPassword)
    private val emptyPasswordRequest = LoginUserRequest(validUsername, "")

    private val route = "/login"

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
