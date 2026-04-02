package xyz.mitzie

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import xyz.mitzie.database.configureDatabase
import xyz.mitzie.security.configureSecurity

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDatabase()
    configureContentNegotiation()
    configureSecurity()
    configureRouting()
}

fun Application.configureContentNegotiation() {
    install(ContentNegotiation) {
        json()
    }
}
