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
}
