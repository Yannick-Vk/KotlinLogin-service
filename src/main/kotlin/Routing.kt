package xyz.mitzie

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import xyz.mitzie.dto.LoginResult
import xyz.mitzie.dto.RegisterUserRequest
import xyz.mitzie.dto.LoginUserRequest
import xyz.mitzie.dto.UserDTO
import xyz.mitzie.dto.RegisterResult
import xyz.mitzie.services.loginUser
import xyz.mitzie.services.registerUser

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
            val user = call.receive<LoginUserRequest>()

            when (val result = loginUser(user, jwtConfig)) {
                is LoginResult.Success -> call.respond(HttpStatusCode.OK, result)
                is LoginResult.UnknownError -> call.respond(HttpStatusCode.InternalServerError, "An unknown error occurred")
            }
        }
        // User has to be loggedIn
        authenticate("jwt-auth") {
            // Send back the user data
            get("/user") {
                val principal = call.principal<JWTPrincipal>()
                // Get username from token claim
                val username = principal!!.payload.getClaim("username").asString()
                val email= principal.payload.getClaim("email").asString()

                call.respond(UserDTO(username, email))
            }
        }
    }
}
