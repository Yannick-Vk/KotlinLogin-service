package xyz.mitzie.dto

import kotlinx.serialization.Serializable

@Serializable
class RegisterUserRequest(val username: String, val email: String, val password: String)