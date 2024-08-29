package com.example.rodexapp.api

import com.example.rodexapp.model.LoginRequest
import com.example.rodexapp.model.LoginResponse
import com.example.rodexapp.model.RegisterRequest
import com.example.rodexapp.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("user/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("user/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
}
