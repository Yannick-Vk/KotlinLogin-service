package xyz.mitzie

import io.ktor.server.application.Application
import io.ktor.server.application.application
import io.ktor.server.application.ApplicationEnvironment

data class JwtConfig(val audience: String, val domain: String, val realm: String, val secret: String)

fun Application.getJwtConfig(): JwtConfig {
    val audience = environment.config.property("jwt.audience").getString()
    val domain = environment.config.property("jwt.domain").getString()
    val realm = environment.config.property("jwt.realm").getString()
    val secret = environment.config.property("jwt.secret").getString()
    return JwtConfig(audience, domain, realm, secret)
}