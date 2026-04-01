package xyz.mitzie.services

import xyz.mitzie.dto.UserClaim

fun resetPassword(user: UserClaim): PasswordResetResult {
    return PasswordResetResult.UnknownError("Not implemented yet")
}

sealed class PasswordResetResult {
    @kotlinx.serialization.Serializable
    data class Success(val token: String) : PasswordResetResult()
    data class ValidationError(val message: String) : PasswordResetResult()
    data class DatabaseError(val message: String) : PasswordResetResult()
    // Default, for unexpected errors
    data class UnknownError(val message: String) : PasswordResetResult()
}