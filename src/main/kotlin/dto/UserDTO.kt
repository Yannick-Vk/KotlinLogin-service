package xyz.mitzie.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(val username: String, val email: String)