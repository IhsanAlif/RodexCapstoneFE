package com.example.rodexapp.api

import com.example.rodexapp.model.Damage
import com.example.rodexapp.model.DamageRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface DamageService {
    @GET("image/damages")
    suspend fun getAll(): List<Damage>

    @POST("image/upload")
    suspend fun upload(@Body damageRequest: DamageRequest): Damage
}
