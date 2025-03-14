package com.example.planer.database

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import kotlin.collections.get

class JsonConverter() {

    private val gson = Gson()

    suspend fun exportDatabaseToJson(database: PlanerDatabase, filePath: String): File? {
        var file: File? = null
        withContext(Dispatchers.IO) {
            try {
                // Daten aus der Datenbank abrufen
                val planerDao = database.planerDao()

                val meals = planerDao.getAllMeals()
                val mealPlans = planerDao.getMealPlan()

                // Daten in JSON umwandeln
                val mealsJson = gson.toJson(meals)
                val mealPlansJson = gson.toJson(mealPlans)

                // JSON in Datei schreiben
                file = File(filePath)
                FileWriter(file).use { writer ->
                    writer.write("{\"meals\": $mealsJson, \"mealPlans\": $mealPlansJson}")
                }

                Log.d("DatabaseToJsonConverter", "Database exported to JSON: $filePath")
                //return@withContext file
            } catch (e: Exception) {
                Log.e("DatabaseToJsonConverter", "Error exporting database to JSON", e)
            }
        }
        return file
    }

    suspend fun importDatabaseFromJson(database: PlanerDatabase, filePath: String) {
        withContext(Dispatchers.IO) {
            try {
                // JSON aus Datei lesen
                val file = File(filePath)
                val jsonString = FileReader(file).readText()

                // JSON in Daten umwandeln
                val jsonObject = gson.fromJson(jsonString, Map::class.java)

                val mealsJson = gson.toJson(jsonObject["meals"])
                val mealPlansJson = gson.toJson(jsonObject["mealPlans"])

                val mealType = object : TypeToken<List<MealEntity>>() {}.type
                val mealPlanType = object : TypeToken<List<MealPlanEntity>>() {}.type

                val meals: List<MealEntity> = gson.fromJson(mealsJson, mealType)
                val mealPlans: List<MealPlanEntity> = gson.fromJson(mealPlansJson, mealPlanType)

                // Daten in die Datenbank einfügen
                val planerDao = database.planerDao()

                //todo alte löschen
                planerDao.insertAllMeals(meals)
                planerDao.insertAllMealPlans(mealPlans)

                Log.d("DatabaseToJsonConverter", "Database imported from JSON: $filePath")
            } catch (e: Exception) {
                Log.e("DatabaseToJsonConverter", "Error importing database from JSON", e)
            }
        }
    }
}