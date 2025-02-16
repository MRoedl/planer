package com.example.planer.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals_table")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "ingredients") val ingredients: String,
    @ColumnInfo(name = "dietaryTags") val dietaryTags: String,
    @ColumnInfo(name = "lastEaten") val lastEaten: Long?
)
