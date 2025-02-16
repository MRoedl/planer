package com.example.planer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MealEntity::class, MealPlanEntity::class], version = 8)
abstract class PlanerDatabase : RoomDatabase() {

    abstract fun planerDao(): PlanerDao

    companion object {
        @Volatile
        private var INSTANCE: PlanerDatabase? = null

        fun getDatabase(context: Context): PlanerDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PlanerDatabase::class.java,
                    "meal_db"
                ).fallbackToDestructiveMigration()
                    .build().also {
                    INSTANCE = it
                }
            }
        }
    }
}