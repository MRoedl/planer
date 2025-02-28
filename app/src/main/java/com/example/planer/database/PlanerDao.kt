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

    @Delete
    suspend fun delete(meal: MealEntity)

    @Query("SELECT * FROM meals_table")
    suspend fun getAllMeals(): List<MealEntity>

    @Query("DELETE FROM meals_table")
    suspend fun deleteAllMeals()

    @Query("DELETE FROM meals_table WHERE id = :id")
    suspend fun deleteMealById(id: Int)

    @Query("SELECT * FROM meals_table WHERE id = :id")
    suspend fun getMealById(id: Int): MealEntity

    @Update
    suspend fun updateMeal(meal: MealEntity)

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
    suspend fun deleteMealPlanById(id: Int)

    @Query("DELETE FROM SQLITE_SEQUENCE WHERE name='mealPlan_table'")
    suspend fun resetAutoIncrement()
}