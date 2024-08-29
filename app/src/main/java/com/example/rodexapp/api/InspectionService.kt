package com.example.rodexapp.api

import com.example.rodexapp.model.Inspection
import com.example.rodexapp.model.InspectionRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface InspectionService {
    @GET("inspection/get")
    suspend fun getAll(): List<Inspection>

    @POST("inspection/new")
    suspend fun new(@Body inspectionRequest: InspectionRequest): Inspection
}
