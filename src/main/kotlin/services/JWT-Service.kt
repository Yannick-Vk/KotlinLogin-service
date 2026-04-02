package xyz.mitzie.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.slf4j.LoggerFactory
import xyz.mitzie.dto.TokenResponse
import xyz.mitzie.dto.UserClaim
import xyz.mitzie.dto.UserDTO
import xyz.mitzie.security.JwtConfig
import java.util.*

fun calculateExpiration(tokenLifetimeMs: Long): Date {
    // Set token expiration time in ms
    return Date(System.currentTimeMillis() + tokenLifetimeMs)
}

fun generateAccessToken(jwtConfig: JwtConfig, user: UserDTO): String {
    return generateGenericToken(jwtConfig, user, jwtConfig.accessTokenExpiration)
}

fun generateRefreshToken(jwtConfig: JwtConfig, user: UserDTO): String {
    return generateGenericToken(jwtConfig, user, jwtConfig.refreshTokenExpiration)
}

fun generateFullToken(jwtConfig: JwtConfig, user: UserDTO): TokenResponse {
    return TokenResponse(generateAccessToken(jwtConfig, user), generateRefreshToken(jwtConfig, user))
}

private fun generateGenericToken(jwtConfig: JwtConfig, user: UserDTO, lifetime: Long): String {
    val expiresAt = calculateExpiration(lifetime)
    val issuedAt = Date()

    // Generate token
    val token = JWT.create()
        .withAudience(jwtConfig.audience)
        .withIssuer(jwtConfig.domain)
        .withClaim("username", user.username)
        .withClaim("email", user.email)
        .withExpiresAt(expiresAt)
        .withIssuedAt(issuedAt)
        .withJWTId(UUID.randomUUID().toString())
        .sign(Algorithm.HMAC256(jwtConfig.secret))

    return token
}

fun getUserFromToken(call: ApplicationCall): UserClaim {
    val principal = call.principal<JWTPrincipal>()
    // Get username from token claim
    val username = principal!!.payload.getClaim("username").asString()
    val email = principal.payload.getClaim("email").asString()
    val expiresAt = principal.payload.getClaim("exp").asLong()

    return UserClaim(username, email, expiresAt)
}

fun verifyTokenAndGetClaims(jwtConfig: JwtConfig, tokenString: String): UserDTO? {
    return try {
        val verifier = JWT.require(Algorithm.HMAC256(jwtConfig.secret))
            .withAudience(jwtConfig.audience)
            .withIssuer(jwtConfig.domain)
            .build()

        val decodedToken = verifier.verify(tokenString)
        val username = decodedToken.getClaim("username").asString()
        val email = decodedToken.getClaim("email").asString()
        if (username == null || email == null) {
            return null
        }

        UserDTO(username, email)
    } catch (_: JWTVerificationException) {
        null
    } catch (_: Exception) {
        null
    }
}
