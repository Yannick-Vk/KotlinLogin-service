package xyz.mitzie

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.Date

fun Application.configureRouting() {

    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtDomain = environment.config.property("jwt.domain").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        post("/login") {
            val user = call.receive<UserCredentialsDTO>()
            // Handle credentials

            // Set token expiration time
            val expiresAt = Date(System.currentTimeMillis() + 60000);
            // Generate token
            val token = JWT.create()
                .withAudience(jwtAudience)
                .withIssuer(jwtDomain)
                .withClaim("username", user.username)
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC256(jwtSecret))

            call.respond(hashMapOf("token" to token))
        }
        // User has to be loggedIn
        authenticate("jwt-auth") {
            get("/user") {
                val principal = call.principal<JWTPrincipal>()
                // Get username from token claim
                val username = principal!!.payload.getClaim("username").asString()

                call.respondText("Welcome $username! You are logged in this protected /user route!")
            }
        }
    }
}
