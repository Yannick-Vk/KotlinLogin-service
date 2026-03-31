package xyz.mitzie.dto

import kotlinx.serialization.Serializable

@Serializable
class LoginUserRequest(val username: String, val password: String)