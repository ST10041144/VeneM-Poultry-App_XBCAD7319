package com.example.venempoultry

// Model for SignUp Response
data class SignUpResponse(
    val id: String,
    val email: String
)

// Model for SignIn Response
data class SignInResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)

// User model, shared between SignIn and other responses
data class User(
    val id: String,
    val email: String,
    val role: String
)

// Flock model
data class Flock(
    val id: Int,
    val species: String,
    val batchSize: Int,
    val ageInWeeks: Int
)
