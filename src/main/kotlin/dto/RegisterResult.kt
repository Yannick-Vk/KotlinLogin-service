package xyz.mitzie.dto

sealed class RegisterResult {
    data class Success(val user: UserDTO) : RegisterResult()
    data class ValidationError(val message: String) : RegisterResult()
    data class ConflictError(val message: String) : RegisterResult() // For duplicate username/email
    data class DatabaseError(val message: String) : RegisterResult()

    // Default, for unexpected errors
    object UnknownError : RegisterResult()
}