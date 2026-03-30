package xyz.mitzie

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import xyz.mitzie.dto.RegisterUserRequest
import xyz.mitzie.dto.UserCredentialsDTO
import xyz.mitzie.dto.UserDTO
import xyz.mitzie.services.RegisterResult
import xyz.mitzie.services.registerUser
import java.util.Date

fun Application.configureRouting() {

    val jwtConfig = getJwtConfig()

    routing {
        get("/") {
            call.respondText("Mitzie Auth Service up and running!")
        }

        post("register") {
            val request = call.receive<RegisterUserRequest>()

            when (val result = registerUser(request)) {
                is RegisterResult.Success -> call.respond(HttpStatusCode.Created, result.user)
                is RegisterResult.ValidationError -> call.respond(HttpStatusCode.BadRequest, result.message)
                is RegisterResult.ConflictError -> call.respond(HttpStatusCode.Conflict, result.message)
                is RegisterResult.DatabaseError -> call.respond(HttpStatusCode.InternalServerError, result.message)
                is RegisterResult.UnknownError -> call.respond(HttpStatusCode.InternalServerError, "An unknown error occurred")
            }
        }

        post("/login") {
            val user = call.receive<UserCredentialsDTO>()
            // Handle credentials

            // Set token expiration time in ms, 60sec
            val expiresAt = Date(System.currentTimeMillis() + 60_000)
            // Generate token
            val token = JWT.create()
                .withAudience(jwtConfig.audience)
                .withIssuer(jwtConfig.domain)
                .withClaim("username", user.username)
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC256(jwtConfig.secret))

            call.respond(hashMapOf("token" to token))
        }
        // User has to be loggedIn
        authenticate("jwt-auth") {
            // Send back the user data
            get("/user") {
                val principal = call.principal<JWTPrincipal>()
                // Get username from token claim
                val username = principal!!.payload.getClaim("username").asString()

                call.respond(UserDTO(username, "no email saved"))
            }
        }
    }
}
