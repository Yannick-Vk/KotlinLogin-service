package xyz.mitzie

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureContentNegotiation()
    configureSecurity()
    configureRouting()
}

fun Application.configureContentNegotiation() {
    install(ContentNegotiation) {
        json()
    }
}
