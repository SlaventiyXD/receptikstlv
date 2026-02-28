package com.example.receptikstlv.data.models

data class Ingredient(
    val id: Int = 0,
    val recipeId: Int,
    val name: String,
    val amount: String
)