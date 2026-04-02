package xyz.mitzie.dto

import kotlinx.serialization.Serializable

sealed class LoginResult {
    @Serializable
    data class Success(val tokens: TokenResponse) : LoginResult()
    @Serializable
    data class ValidationError(val message: String) : LoginResult()
    @Serializable
    data class DatabaseError(val message: String) : LoginResult()
    // Default, for unexpected errors
    @Serializable
    data class UnknownError(val message: String) : LoginResult()
}