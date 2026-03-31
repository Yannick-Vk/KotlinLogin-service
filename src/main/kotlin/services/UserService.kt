package xyz.mitzie.services

import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import xyz.mitzie.CheckPassword
import xyz.mitzie.EncryptPassword
import xyz.mitzie.JwtConfig
import xyz.mitzie.dto.LoginResult
import xyz.mitzie.dto.LoginUserRequest
import xyz.mitzie.dto.RegisterResult
import xyz.mitzie.dto.RegisterUserRequest
import xyz.mitzie.dto.UserDTO
import xyz.mitzie.models.UsersTable

// Check if the parameters are valid
private fun validateRegisterRequest(req: RegisterUserRequest): RegisterResult? {
    // Validate fields are not empty
    if (req.username.isBlank()) return RegisterResult.ValidationError("Username cannot be empty")
    if (req.password.isBlank()) return RegisterResult.ValidationError("Password cannot be empty")
    if (req.email.isBlank()) return RegisterResult.ValidationError("Email cannot be empty")

    // Validate password
    if (req.password.length < 8) return RegisterResult.ValidationError("Password must be at least 8 characters long")

    // validate email
    if (!req.email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}\$")))
        return RegisterResult.ValidationError("Invalid email format")

    return null
}

fun registerUser(req: RegisterUserRequest): RegisterResult {
    val validReq = validateRegisterRequest(req)
    if (validReq != null) {
        return validReq
    }

    try {
        val hashedPassword = EncryptPassword(req.password)
        val createdUserDTO: UserDTO? = transaction {
            val existingUsername = UsersTable.selectAll().where { UsersTable.username eq req.username }.singleOrNull()
            if (existingUsername != null) {
                return@transaction null
            }

            val existingEmail = UsersTable.selectAll().where { UsersTable.email eq req.email }.singleOrNull()
            if (existingEmail != null) {
                return@transaction null
            }

            val insertedId = UsersTable.insert {
                it[username] = req.username
                it[email] = req.email
                it[passwordHash] = hashedPassword
            } get UsersTable.id

            UserDTO(req.username, req.email)
        }

        return createdUserDTO?.let { RegisterResult.Success(it) }
            ?: RegisterResult.ConflictError("Username or Email already exists.")

    } catch (e: ExposedSQLException) {
        // More sophisticated parsing of e.message might be needed for specific SQL error codes
        if (e.message?.contains("duplicate key value violates unique constraint") == true) {
            return RegisterResult.ConflictError("Username or email already exists (database constraint violated).")
        }
        return RegisterResult.DatabaseError("Database error during registration: ${e.message}")
    } catch(_: Exception) {
        return RegisterResult.UnknownError
    }
}

fun loginUser(req: LoginUserRequest, jwtConfig: JwtConfig): LoginResult {
    // Handle credentials
    // Validate fields are not empty
    if (req.username.isBlank()) return LoginResult.ValidationError("Username cannot be empty")
    if (req.password.isBlank()) return LoginResult.ValidationError("Password cannot be empty")

    try {
        // Find user or null
        val user = transaction {
            UsersTable.select(UsersTable.username eq req.username).singleOrNull()
        }
        if (user == null) return LoginResult.ValidationError("Invalid username or password.")
        // Validate Password
        val passwordCorrect = CheckPassword(req.password, user[UsersTable.passwordHash])
        if (!passwordCorrect) return LoginResult.ValidationError("Invalid username or password.")

        // User is authenticated, generate a token
        val token = generateToken(jwtConfig, UserDTO(user[UsersTable.email], user[UsersTable.email]))

        return LoginResult.Success(token)
    } catch (e: ExposedSQLException) {
        return LoginResult.DatabaseError("Database error during registration: ${e.message}")
    } catch (e: Exception) {
        return LoginResult.UnknownError("Unknown error: ${e.message?: "An unknown error occurred"}")
    }
}
