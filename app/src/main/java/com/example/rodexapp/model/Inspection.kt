package com.example.rodexapp.model

data class Inspection(
    val id: String,
    val location_start: String,
    val type_of_road_surface: String,
    val road_class: String,
    val address: String,
    val additional: String,
    val name_of_road: String,
    val name_of_officer: String,
    val length_of_road: String,
    val width_of_road: String,
    val location_end: String? = null,
    val status: String,
    val date_started: String,
    var imageUrl: String? = null,
    var count_damages_type_0: String? = null,
    var count_damages_type_1: String? = null,
    var count_damages_type_2: String? = null,
    var count_damages_type_3: String? = null,
    var count_damages: String? = null
)
