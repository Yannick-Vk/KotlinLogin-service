package xyz.mitzie.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserClaim(val username: String, val email: String, val expiresAt: Long)