package com.example.planer.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals_table")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "popularity") val popularity: Int,
    @ColumnInfo(name = "monday") val monday: Boolean,
    @ColumnInfo(name = "tuesday") val tuesday: Boolean,
    @ColumnInfo(name = "wednesday") val wednesday: Boolean,
    @ColumnInfo(name = "thursday") val thursday: Boolean,
    @ColumnInfo(name = "friday") val friday: Boolean,
    @ColumnInfo(name = "saturday") val saturday: Boolean,
    @ColumnInfo(name = "sunday") val sunday: Boolean,
    @ColumnInfo(name = "lastEaten") val lastEaten: Long?
)
