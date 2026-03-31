package xyz.mitzie.dto

sealed class LoginResult {
    data class Success(val token: String) : LoginResult()

    // Default, for unexpected errors
    object UnknownError : LoginResult()
}