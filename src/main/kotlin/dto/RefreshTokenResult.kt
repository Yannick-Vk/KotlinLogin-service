package xyz.mitzie.dto

import kotlinx.serialization.Serializable

sealed class RefreshTokenResult {
    @Serializable
    data class Success(val tokens: TokenResponse) : RefreshTokenResult()
    @Serializable
    data class InvalidToken(val message: String) : RefreshTokenResult()
    @Serializable
    data class UnknownError(val message: String) : RefreshTokenResult()
}
