package xyz.mitzie

import kotlinx.serialization.Serializable

@Serializable
class UserCredentialsDTO(val username: String, val password: String)