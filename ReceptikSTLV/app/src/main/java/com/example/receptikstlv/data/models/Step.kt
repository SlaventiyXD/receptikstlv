package com.example.receptikstlv.data.models

data class Step(
    val id: Int = 0,
    val recipeId: Int,
    val stepNumber: Int,
    val description: String
)