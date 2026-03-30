package xyz.mitzie

import org.mindrot.jbcrypt.BCrypt

fun EncryptPassword(password: String) : String {
    return BCrypt.hashpw(password, BCrypt.gensalt())
}

fun CheckPassword(password: String, hashedPassword: String) : Boolean {
    return BCrypt.checkpw(password, hashedPassword)
}