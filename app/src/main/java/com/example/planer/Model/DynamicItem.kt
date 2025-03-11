package com.example.planer.Model

// Basisklasse oder Interface
sealed class DynamicItem {
    data class TextItem(val text: String) : DynamicItem()
    data class ImageItem(val imageUrl: String) : DynamicItem()
    data class MealPlanItem(val id: Long, val meal: Long, val maxDate: Long?) : DynamicItem()

    data class MealListItem(
        val pColor: Int,
        val ptvMealText: String,
        val pbtnEditTag: Long?,
        val ptVText: String,
        val pbtnEditId: Int
    ) : DynamicItem()
}