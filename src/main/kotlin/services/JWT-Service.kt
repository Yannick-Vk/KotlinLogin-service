package xyz.mitzie.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import xyz.mitzie.JwtConfig
import xyz.mitzie.dto.UserClaim
import xyz.mitzie.dto.UserDTO
import java.util.Date

const val token_lifetime_ms = 60_000 * 5 // 60 sec

fun calculateExpiration(): Date {
    // Set token expiration time in ms
    return Date(System.currentTimeMillis() + token_lifetime_ms)
}

fun generateToken(jwtConfig: JwtConfig, user: UserDTO) :String {
    val expiresAt = calculateExpiration()
    // Generate token
    val token = JWT.create()
        .withAudience(jwtConfig.audience)
        .withIssuer(jwtConfig.domain)
        .withClaim("username", user.username)
        .withClaim("email", user.email)
        .withExpiresAt(expiresAt)
        .sign(Algorithm.HMAC256(jwtConfig.secret))

    return token
}

fun getUserFromToken(call: ApplicationCall) : UserClaim {
    val principal = call.principal<JWTPrincipal>()
    // Get username from token claim
    val username = principal!!.payload.getClaim("username").asString()
    val email= principal.payload.getClaim("email").asString()
    val expiresAt = principal.payload.getClaim("exp").asLong()

    return UserClaim(username, email, expiresAt)
}