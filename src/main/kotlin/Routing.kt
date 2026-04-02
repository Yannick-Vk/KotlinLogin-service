package xyz.mitzie

import io.ktor.http.*
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import xyz.mitzie.dto.LoginResult
import xyz.mitzie.dto.LoginUserRequest
import xyz.mitzie.dto.RefreshTokenRequest
import xyz.mitzie.dto.RegisterResult
import xyz.mitzie.dto.RegisterUserRequest
import xyz.mitzie.services.getUserFromToken
import xyz.mitzie.services.loginUser
import xyz.mitzie.services.registerUser

fun Application.configureRouting() {

    val jwtConfig = getJwtConfig()

    routing {
        get("/") {
            call.respondText("Mitzie Auth Service up and running!")
        }

        post("/register") {
            val request = call.receive<RegisterUserRequest>()

            when (val result = registerUser(request)) {
                is RegisterResult.Success -> call.respond(HttpStatusCode.Created, result.user)
                is RegisterResult.ValidationError -> call.respond(HttpStatusCode.BadRequest, result.message)
                is RegisterResult.ConflictError -> call.respond(HttpStatusCode.Conflict, result.message)
                is RegisterResult.DatabaseError -> call.respond(HttpStatusCode.InternalServerError, result.message)
                is RegisterResult.UnknownError -> call.respond(HttpStatusCode.InternalServerError, result.message)
            }
        }

        post("/login") {
            val user = call.receive<LoginUserRequest>()

            when (val result = loginUser(user, jwtConfig)) {
                is LoginResult.Success -> call.respond(HttpStatusCode.OK, result)
                is LoginResult.ValidationError -> call.respond(HttpStatusCode.BadRequest, result.message)
                is LoginResult.DatabaseError -> call.respond(HttpStatusCode.InternalServerError, result.message)
                is LoginResult.UnknownError -> call.respond(HttpStatusCode.InternalServerError, result.message)
            }
        }
        post("/refresh") {
            val token = call.receive<RefreshTokenRequest>()
            call.respond(HttpStatusCode.OK, token)
        }
        // User has to be loggedIn
        authenticate("jwt-auth") {
            // Send back the user data
            get("/user") {
                val user = getUserFromToken(call)
                call.respond(user)
            }
        }
    }
}
