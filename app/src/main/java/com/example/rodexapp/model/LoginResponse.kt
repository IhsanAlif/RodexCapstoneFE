package com.example.rodexapp.model

data class LoginResponse(
    val userId: String,
    val username: String,
    val email: String,
    val role: String
)
