package xyz.mitzie.dto

sealed class LoginResult {
    @kotlinx.serialization.Serializable
    data class Success(val token: String) : LoginResult()
    data class ValidationError(val message: String) : LoginResult()
    // Default, for unexpected errors
    data class UnknownError(val message: String) : LoginResult()
}