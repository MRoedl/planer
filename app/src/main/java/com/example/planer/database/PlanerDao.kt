package com.example.planer.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

@Dao
interface PlanerDao {
    // Meals_Table
    @Insert
    suspend fun insert(meal: MealEntity)

    @Update
    suspend fun updateMeal(meal: MealEntity)

    @Delete
    suspend fun delete(meal: MealEntity)

    @Query("SELECT * FROM meals_table")
    suspend fun getAllMeals(): List<MealEntity>

    @Query("DELETE FROM meals_table")
    suspend fun deleteAllMeals()

    @Query("DELETE FROM meals_table WHERE id = :id")
    suspend fun deleteMealById(id: Long)

    @Query("SELECT * FROM meals_table WHERE id = :id")
    suspend fun getMealById(id: Long): MealEntity

    @Query("SELECT * FROM meals_table WHERE monday = true")
    suspend fun getMealsByMonday(): List<MealEntity>

    @Query("SELECT * FROM meals_table WHERE tuesday = true")
    suspend fun getMealsByTuesday(): List<MealEntity>

    @Query("SELECT * FROM meals_table WHERE wednesday = true")
    suspend fun getMealsByWednesday(): List<MealEntity>

    @Query("SELECT * FROM meals_table WHERE thursday = true")
    suspend fun getMealsByThursday(): List<MealEntity>

    @Query("SELECT * FROM meals_table WHERE friday = true")
    suspend fun getMealsByFriday(): List<MealEntity>

    @Query("SELECT * FROM meals_table WHERE saturday = true")
    suspend fun getMealsBySaturday(): List<MealEntity>

    @Query("SELECT * FROM meals_table WHERE sunday = true")
    suspend fun getMealsBySunday(): List<MealEntity>

    // MealPlan_Table
    @Insert
    suspend fun insert(mealPlanEntity: MealPlanEntity)

    @Query("SELECT * FROM mealPlan_table")
    suspend fun getMealPlan(): List<MealPlanEntity>

    @Query("SELECT * FROM mealPlan_table LIMIT 1")
    suspend fun getFirstMealPlan(): MealPlanEntity

    @Query("DELETE FROM mealPlan_table")
    suspend fun deleteMealPlan()

    @Query("DELETE FROM mealPlan_table WHERE id = :id")
    suspend fun deleteMealPlanById(id: Long)

    @Query("DELETE FROM SQLITE_SEQUENCE WHERE name='mealPlan_table'")
    suspend fun resetAutoIncrement()
}

enum class Day {
    monday,
    tuesday,
    wednesday,
    thursday,
    friday,
    saturday,
    sunday
}