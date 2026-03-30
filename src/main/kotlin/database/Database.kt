package xyz.mitzie.database

import io.ktor.server.application.*
import io.ktor.http.ContentType
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import xyz.mitzie.models.UsersTable

fun Application.configureDatabase() {
    val dbConfig = environment.config.config("ktor.database")

    val hikariConfig = HikariConfig().apply {
        jdbcUrl = dbConfig.property("url").getString()
        username = dbConfig.property("user").getString()
        password = dbConfig.property("password").getString()
        driverClassName = dbConfig.property("jdbcDriver").getString()
        maximumPoolSize = dbConfig.property("poolSize").getString().toInt()
        isAutoCommit = dbConfig.property("autoCommit").getString().toBoolean()
        transactionIsolation = dbConfig.property("transactionIsolation").getString()
        validate()
    }

    val dataSource = HikariDataSource(hikariConfig)
    Database.connect(dataSource)

    transaction {
        SchemaUtils.create(UsersTable)
    }
}