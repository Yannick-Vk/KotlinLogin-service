package xyz.mitzie

import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.server.testing.*
import xyz.mitzie.dto.RegisterUserRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RegistrationTest {

    private val validUsername = "newUser"
    private val validEmail = "newuser@example.com"
    private val validPassword = "newPassword"

    // Requests
    private val emptyUsernameRequest = RegisterUserRequest("", validEmail, validPassword)
    private val emptyEmailRequest = RegisterUserRequest(validUsername, "", validPassword)
    private val emptyPasswordRequest = RegisterUserRequest(validUsername, validEmail, "")
    private val passwordTooShortRequest = RegisterUserRequest(validUsername, validEmail, "short")

    private val invalidEmailRequest = RegisterUserRequest(validUsername, "not-an-email", validPassword)

    @Test
    fun testRegistrationSuccess() = testApplication {
        environment { config = testConfig }
        application { module() }

        val client = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        val requestBody = RegisterUserRequest(validUsername, validEmail, validPassword)

        client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
            val responseBody = bodyAsText()
            assertTrue(responseBody.contains(validUsername))
            assertTrue(responseBody.contains(validEmail))
        }

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

        client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(emptyUsernameRequest)
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            assertEquals("Username cannot be empty", bodyAsText())
        }
    }

    @Test
    fun testRegisterWithEmptyEmail() = testApplication {
        environment { config = testConfig }
        application { module() }

        val client = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(emptyEmailRequest)
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            assertEquals("Email cannot be empty", bodyAsText())
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

        client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(emptyPasswordRequest)
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            assertEquals("Password cannot be empty", bodyAsText())
        }
    }

    @Test
    fun testRegisterWithPasswordTooShort() = testApplication {
        environment { config = testConfig }
        application { module() }

        val client = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(passwordTooShortRequest)
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            assertEquals("Password must be at least 8 characters long", bodyAsText())
        }
    }

    @Test
    fun testRegisterWithInvalidEmail() = testApplication {
        environment { config = testConfig }
        application { module() }

        val client = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(invalidEmailRequest)
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            assertEquals("Invalid email format", bodyAsText())
        }
    }
}
