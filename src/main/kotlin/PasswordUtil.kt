package xyz.mitzie

import org.mindrot.jbcrypt.BCrypt

fun encryptPassword(password: String) : String {
    return BCrypt.hashpw(password, BCrypt.gensalt())
}

fun validatePassword(password: String, hashedPassword: String) : Boolean {
    return BCrypt.checkpw(password, hashedPassword)
}