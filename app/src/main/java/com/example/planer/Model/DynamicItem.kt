package com.example.planer.Model

// Basisklasse oder Interface
sealed class DynamicItem {
    data class MealListItem(
        val meal: String,
        val btnTag: Long?
    ) : DynamicItem()

    data class MealPlanItem(
        val pColor: Int,
        val ptvMealText: String,
        val pbtnEditTag: Long?,
        val ptVText: String,
        val pbtnEditId: Int
    ) : DynamicItem()
}