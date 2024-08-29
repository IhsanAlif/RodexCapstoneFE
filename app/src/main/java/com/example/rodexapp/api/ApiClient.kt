package com.example.rodexapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://us-central1-capstone-426015.cloudfunctions.net/api/"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authService: AuthService = retrofit.create(AuthService::class.java)
    val inspectionService: InspectionService = retrofit.create(InspectionService::class.java)
    val damageService: DamageService = retrofit.create(DamageService::class.java)
}
