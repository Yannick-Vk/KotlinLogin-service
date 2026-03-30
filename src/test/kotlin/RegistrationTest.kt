package xyz.mitzie

import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.server.testing.*
import xyz.mitzie.dto.RegisterUserRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RegistrationTest {

    private val valid_username = "newUser"
    private val valid_email = "newuser@example.com"
    private val valid_password = "newPassword"

    @Test
    fun testRegistrationSuccess() = testApplication {
        environment {
            config = testConfig
        }
        application {
            module()
        }

        val client = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        val requestBody = RegisterUserRequest(valid_username, valid_email, valid_password)

        client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
            val responseBody = bodyAsText()
            assertTrue(responseBody.contains(valid_username))
            assertTrue(responseBody.contains(valid_email))
        }

    }
}
