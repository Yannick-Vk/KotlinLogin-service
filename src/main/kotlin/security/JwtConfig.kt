package xyz.mitzie.security

import io.ktor.server.application.*

data class JwtConfig(
    val audience: String,
    val domain: String,
    val realm: String,
    val secret: String,
    val accessTokenExpiration: Long,
    val refreshTokenExpiration: Long
)

fun Application.getJwtConfig(): JwtConfig {
    val audience = environment.config.property("jwt.audience").getString()
    val domain = environment.config.property("jwt.domain").getString()
    val realm = environment.config.property("jwt.realm").getString()
    val secret = environment.config.property("jwt.secret").getString()
    val accessTokenExpiration: Long = environment.config.property("jwt.accessTokenExpiration").getString().toLong()
    val refreshTokenExpiration: Long = environment.config.property("jwt.refreshTokenExpiration").getString().toLong()

    return JwtConfig(audience, domain, realm, secret, accessTokenExpiration, refreshTokenExpiration)
}