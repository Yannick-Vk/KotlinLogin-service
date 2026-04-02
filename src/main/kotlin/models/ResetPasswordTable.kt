package xyz.mitzie.models

import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object ResetPasswordTable : IntIdTable("reset_passwords") {
    val userid = reference("user_id", UsersTable.id, ReferenceOption.CASCADE)
    val tokenHash = varchar("token_hash", 255).uniqueIndex()
    val expiresAt = datetime("expires_at")
    val used = bool("used")
}