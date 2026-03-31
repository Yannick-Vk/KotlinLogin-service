package xyz.mitzie.services

import xyz.mitzie.dto.UserDTO

sealed class RegisterResult {
    data class Success(val user: UserDTO) : RegisterResult()
    data class ValidationError(val message: String) : RegisterResult()
    data class ConflictError(val message: String) : RegisterResult() // For duplicate username/email
    data class DatabaseError(val message: String) : RegisterResult()

    // Default, for unexpected errors
    object UnknownError : RegisterResult()
}