package com.example.rodexapp.model

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val role: String
)
