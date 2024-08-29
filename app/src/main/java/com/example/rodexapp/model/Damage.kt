package com.example.rodexapp.model

data class Damage(
    val id: String,
    val image: String,
    val inspectionId: String,
    val image_url: String,
    val count_damages_type_0: String,
    val count_damages_type_1: String,
    val count_damages_type_2: String,
    val count_damages_type_3: String,
    var count_damages: String,
    val detected: Boolean,
    val date_created: String
)
