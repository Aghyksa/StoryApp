package com.aghyksa.storyapp.model

data class Story(
    val id : String,
    val name : String,
    val description : String,
    val photoUrl : String,
    val createAt : String,
    val lat : Double,
    val lon : Double
)