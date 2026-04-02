package xyz.mitzie

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import xyz.mitzie.database.configureDatabase
import xyz.mitzie.dto.LoginResult
import xyz.mitzie.dto.LoginUserRequest
import xyz.mitzie.dto.RegisterUserRequest
import xyz.mitzie.dto.TokenResponse
import xyz.mitzie.models.UsersTable
import kotlin.test.assertEquals

object AuthTestHelper {

    const val validUsername = "newUser"
    const val validEmail = "newuser@example.com"
    const val validPassword = "newPassword"

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

    fun clearDatabase(application: Application) {
        application.configureDatabase() // Ensure database is connected and schema created
        transaction {
            UsersTable.deleteAll()
        }
    }
}
