package com.example.rodexapp.model

data class InspectionRequest(
    val name_of_road: String,
    val name_of_officer: String,
    val length_of_road: String,
    val width_of_road: String,
    val location_start: String,
    val type_of_road_surface: String,
)
