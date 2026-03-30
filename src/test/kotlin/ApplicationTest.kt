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


class ApplicationTest {

    private val testConfig = MapApplicationConfig(
        "ktor.database.url" to "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "ktor.database.jdbcDriver" to "org.h2.Driver",
        "ktor.database.user" to "test",
        "ktor.database.password" to "test",
        "ktor.database.poolSize" to "10",
        "ktor.database.autoCommit" to "false",
        "ktor.database.transactionIsolation" to "TRANSACTION_REPEATABLE_READ",
        "jwt.secret" to "test-secret-long-enough-for-hashing-algorithms-min-32-chars", // Use a sufficiently long secret for testing
        "jwt.domain" to "http://localhost:8080",
        "jwt.audience" to "users",
        "jwt.realm" to "Access to 'users' service"
    )

    private fun TestApplication.createJsonClient() = createClient {
        install(ClientContentNegotiation) {
            json()
        }
    }

    @Test
    fun testRoot() = testApplication {
        environment {
            config = testConfig
        }
        application {
            module()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

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
        val requestBody = RegisterUserRequest("newUser", "newuser@example.com","newPassword")

        client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
            val responseBody = bodyAsText()
            assertTrue(responseBody.contains("newUser"))
            assertTrue(responseBody.contains("newuser@example.com"))
        }

    }
}
