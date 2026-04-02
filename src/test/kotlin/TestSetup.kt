package xyz.mitzie

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation

val testConfig = MapApplicationConfig(
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
    "jwt.realm" to "Access to 'users' service",
    "jwt.accessTokenExpiration" to "60000",      // (1 minute)
    "jwt.refreshTokenExpiration" to "604800000",  // (7 days)
)

fun TestApplication.createJsonClient() = createClient {
    install(ClientContentNegotiation) {
        json()
    }
}

const val validUsername = "newUser"
const val validEmail = "newuser@example.com"
const val validPassword = "newPassword"

class TestSetup {
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
}