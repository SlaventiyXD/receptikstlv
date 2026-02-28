package com.example.receptikstlv.data.models
enum class Category {
    BREAKFAST, LUNCH, DINNER, DESSERT
}

enum class Complexity {
    EASY, MEDIUM, HARD
}

data class Recipe(
    val id: Int = 0,
    val title: String,
    val category: Category,
    val timeMinutes: Int,
    val complexity: Complexity,
    val photoUri: String? = null,
    val isFavorite: Boolean = false
)