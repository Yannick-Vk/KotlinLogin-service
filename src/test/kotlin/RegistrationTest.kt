package xyz.mitzie

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import xyz.mitzie.AuthTestHelper.validEmail
import xyz.mitzie.AuthTestHelper.validPassword
import xyz.mitzie.AuthTestHelper.validUsername
import xyz.mitzie.dto.RegisterUserRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RegistrationTest {
    // Requests
    private val emptyUsernameRequest = RegisterUserRequest("", validEmail, validPassword)
    private val emptyEmailRequest = RegisterUserRequest(validUsername, "", validPassword)
    private val emptyPasswordRequest = RegisterUserRequest(validUsername, validEmail, "")
    private val passwordTooShortRequest = RegisterUserRequest(validUsername, validEmail, "short")
    private val invalidEmailRequest = RegisterUserRequest(validUsername, "not-an-email", validPassword)

    private val baseDuplicateUsername = "duplicateUser"
    private val baseDuplicateEmail = "dupelicate@example.com"

    private val route = "/register"

    @Test
    fun testRegistrationSuccess() = withTestApplicationSetup { client ->
        val requestBody = RegisterUserRequest(validUsername, validEmail, validPassword)

        client.post(route) {
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
    fun testRegisterWithEmptyUsername() = withTestApplicationSetup { client ->
        client.post(route) {
            contentType(ContentType.Application.Json)
            setBody(emptyUsernameRequest)
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            assertEquals("Username cannot be empty", bodyAsText())
        }
    }

    @Test
    fun testRegisterWithEmptyEmail() = withTestApplicationSetup { client ->
        client.post(route) {
            contentType(ContentType.Application.Json)
            setBody(emptyEmailRequest)
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            assertEquals("Email cannot be empty", bodyAsText())
        }
    }

    @Test
    fun testRegisterWithEmptyPassword() = withTestApplicationSetup { client ->
        client.post(route) {
            contentType(ContentType.Application.Json)
            setBody(emptyPasswordRequest)
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            assertEquals("Password cannot be empty", bodyAsText())
        }
    }

    @Test
    fun testRegisterWithPasswordTooShort() = withTestApplicationSetup { client ->
        client.post(route) {
            contentType(ContentType.Application.Json)
            setBody(passwordTooShortRequest)
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            assertEquals("Password must be at least 8 characters long", bodyAsText())
        }
    }

    @Test
    fun testRegisterWithInvalidEmail() = withTestApplicationSetup { client ->
        client.post(route) {
            contentType(ContentType.Application.Json)
            setBody(invalidEmailRequest)
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            assertEquals("Invalid email format", bodyAsText())
        }
    }

    @Test
    fun testRegisterWithDuplicateUsername() = withTestApplicationSetup { client ->
        val firstRequest = RegisterUserRequest(baseDuplicateUsername, "dupemail1@example.com", validPassword)
        client.post(route) {
            contentType(ContentType.Application.Json)
            setBody(firstRequest)
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
        }

        val secondRequest = RegisterUserRequest(baseDuplicateUsername, "dupemail2@example.com", validPassword)
        client.post(route) {
            contentType(ContentType.Application.Json)
            setBody(secondRequest)
        }.apply {
            assertEquals(HttpStatusCode.Conflict, status)
            assertTrue(bodyAsText().contains("Username or Email already exists."))
        }
    }

    @Test
    fun testRegisterWithDuplicateEmail() = withTestApplicationSetup { client ->
        val firstRequest = RegisterUserRequest("user1", baseDuplicateEmail, validPassword)
        client.post(route) {
            contentType(ContentType.Application.Json)
            setBody(firstRequest)
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
        }

        val secondRequest = RegisterUserRequest("user2", baseDuplicateEmail, validPassword)
        client.post(route) {
            contentType(ContentType.Application.Json)
            setBody(secondRequest)
        }.apply {
            assertEquals(HttpStatusCode.Conflict, status)
            assertTrue(bodyAsText().contains("Username or Email already exists."))
        }
    }
}
