package com.example.planer.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "mealPlan_table", foreignKeys = [ForeignKey(entity = MealEntity::class, parentColumns = ["id"], childColumns = ["meal"], onDelete = ForeignKey.CASCADE)])
data class MealPlanEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "meal") val meal: Int // Fremdschlüssel zu MealEntity.id
)
