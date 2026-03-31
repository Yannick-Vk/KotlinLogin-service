package xyz.mitzie.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import xyz.mitzie.JwtConfig
import xyz.mitzie.dto.UserDTO
import java.util.Date

const val token_lifetime_ms = 60_000 // 60 sec

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