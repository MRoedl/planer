package com.example.planer.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlanerDao {
    // Meals_Table
    @Insert
    suspend fun insert(meal: MealEntity)

    @Update
    suspend fun updateMeal(meal: MealEntity)

    @Delete
    suspend fun delete(meal: MealEntity)

    @Insert
    suspend fun insertAllMeals(meals: List<MealEntity>)

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

    @Query("SELECT * FROM meals_table WHERE monday = true ORDER BY lastEaten ASC LIMIT 1")
    suspend fun getOldestMealByMonday(): MealEntity

    @Query("SELECT * FROM meals_table WHERE tuesday = true ORDER BY lastEaten ASC LIMIT 1")
    suspend fun getOldestMealByTuesday(): MealEntity

    @Query("SELECT * FROM meals_table WHERE wednesday = true ORDER BY lastEaten ASC LIMIT 1")
    suspend fun getOldestMealByWednesday(): MealEntity

    @Query("SELECT * FROM meals_table WHERE thursday = true ORDER BY lastEaten ASC LIMIT 1")
    suspend fun getOldestMealByThursday(): MealEntity

    @Query("SELECT * FROM meals_table WHERE friday = true ORDER BY lastEaten ASC LIMIT 1")
    suspend fun getOldestMealByFriday(): MealEntity

    @Query("SELECT * FROM meals_table WHERE saturday = true ORDER BY lastEaten ASC LIMIT 1")
    suspend fun getOldestMealBySaturday(): MealEntity

    @Query("SELECT * FROM meals_table WHERE sunday = true ORDER BY lastEaten ASC LIMIT 1")
    suspend fun getOldestMealBySunday(): MealEntity

    @Query("SELECT count(id) FROM meals_table")
    suspend fun getMealCount(): Int

    @Query("UPDATE meals_table SET lastEaten = :date WHERE id = :id")
    suspend fun updateLastEaten(date: Long?, id: Long)

    @Query("UPDATE meals_table SET lastEaten = null")
    suspend fun resetLastEaten()

    // MealPlan_Table
    @Insert
    suspend fun insert(mealPlanEntity: MealPlanEntity)

    @Update
    suspend fun update(mealPlanEntity: MealPlanEntity)

    @Insert
    suspend fun insertAllMealPlans(mealPlans: List<MealPlanEntity>)

    @Query("UPDATE mealPlan_table SET meal = :meal WHERE id = :id")
    suspend fun updateById(id: Long, meal: Long)

    @Query("SELECT id, meal, max(date) as date FROM mealPlan_table GROUP BY meal")
    suspend fun getLastEaten(): List<MealPlanEntity>

    @Query("SELECT * FROM mealPlan_table")
    suspend fun getMealPlan(): List<MealPlanEntity>

    @Query("SELECT * FROM mealPlan_table WHERE id = :id")
    suspend fun getMealPlanById(id: Long): MealPlanEntity

    @Query("SELECT * FROM mealPlan_table LIMIT 1")
    suspend fun getFirstMealPlan(): MealPlanEntity

    @Query("SELECT * FROM mealPlan_table ORDER BY date DESC LIMIT 1")
    suspend fun getLatestMealPlan(): MealPlanEntity?

    @Query("DELETE FROM mealPlan_table")
    suspend fun deleteAllMealPlans()

    @Query("DELETE FROM mealPlan_table WHERE id = :id")
    suspend fun deleteMealPlanById(id: Long)

    @Query("DELETE FROM mealPlan_table WHERE date > :date")
    suspend fun deleteMealPlanByMinDate(date: Long)

//    @Query("DELETE FROM SQLITE_SEQUENCE WHERE name='mealPlan_table'")
//    suspend fun resetAutoIncrement()
}
