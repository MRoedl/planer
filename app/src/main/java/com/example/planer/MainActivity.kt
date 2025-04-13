package com.example.planer

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.room.Room
import com.example.planer.ViewModel.AppDataStore
import com.example.planer.database.MealEntity
import com.example.planer.database.MealPlanEntity
import com.example.planer.database.PlanerDao
import com.example.planer.database.PlanerDatabase
import com.example.planer.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    lateinit var insertMealPlan: kotlinx.coroutines.Job
    lateinit var loadSettingsJob: kotlinx.coroutines.Job
    private var daysToPlan: Int = 7

    //todo sync mit anderen Geräten

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Room.databaseBuilder(this, PlanerDatabase::class.java, "meal_database").build()

        calcMealPlan()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return when (item.itemId) {
            R.id.action_meals -> {
                Log.d("NACHRICHT", "Settings clicked")
                val navController2 = findNavController(R.id.nav_host_fragment_content_main)
                navController2.navigate(R.id.MealListFragment)
                true
            }
            R.id.action_refresh -> {
                Log.d("NACHRICHT", "Refresh clicked")
                findNavController(R.id.nav_host_fragment_content_main).navigate(navController.currentDestination!!.id)
                true
            }
            R.id.action_recalc -> {
                Log.d("NACHRICHT", "Recalc clicked")
                val builder = AlertDialog.Builder(this)
                builder
                    .setTitle("Plan neu erstellen")
                    .setMessage("Sind Sie sicher, dass Sie den Plan neu erstellen möchten?")
                    .setNegativeButton("Abbrechen") { dialog, which ->
                        dialog.cancel()
                    }
                    .setPositiveButton("Berechnen") { dialog, which ->
                        recalcMealPlan()
                    }
                val dialog = builder.create()
                dialog.show()
                true
            }
            R.id.action_settings -> {
                Log.d("NACHRICHT", "Settings clicked")
                val navController2 = findNavController(R.id.nav_host_fragment_content_main)
                navController2.navigate(R.id.SettingsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    suspend fun loadSettings() {
        val appDataStore = AppDataStore.getInstance(this)

        val days = appDataStore.getDaysToPlan().first()

        if (days == null || days <= 0) {
            appDataStore.setDaysToPlan(7)
            daysToPlan = 7
        } else {
            daysToPlan = days.toInt()
        }
    }

    fun calcMealPlan() {
        val planerDao: PlanerDao = PlanerDatabase.getDatabase(this).planerDao()

        insertMealPlan = lifecycleScope.launch {
            loadSettings()
            updateAllLastEaten()

            val mealPlan: MealPlanEntity? = planerDao.getLatestMealPlan()

            // Get the current date as a timestamp
            var current = Calendar.getInstance()

            if (mealPlan != null) {
                current.timeInMillis = mealPlan.date
                current.add(Calendar.DAY_OF_MONTH, 1)
            }

            var dayInaWeek = Calendar.getInstance()
            dayInaWeek.add(Calendar.DAY_OF_MONTH, daysToPlan)

            val calendar = Calendar.getInstance()
            var weekDay = calendar.get(Calendar.DAY_OF_WEEK)
            var meals: List<MealEntity>? = null

            val formatter = SimpleDateFormat("YYYY.MM.dd", Locale.getDefault())
            var currentFormated = formatter.format(current.time)
            var dayInaWeekFormated = formatter.format(dayInaWeek.time)

            while (currentFormated <= dayInaWeekFormated) {
                weekDay = current.get(Calendar.DAY_OF_WEEK)

                meals = when (weekDay) {
                    Calendar.MONDAY -> planerDao.getMealsByMonday()
                    Calendar.TUESDAY -> planerDao.getMealsByTuesday()
                    Calendar.WEDNESDAY -> planerDao.getMealsByWednesday()
                    Calendar.THURSDAY -> planerDao.getMealsByThursday()
                    Calendar.FRIDAY -> planerDao.getMealsByFriday()
                    Calendar.SATURDAY -> planerDao.getMealsBySaturday()
                    Calendar.SUNDAY -> planerDao.getMealsBySunday()
                    else -> null
                }

                if (meals == null || meals.isEmpty()) {
                    break
                }

                var mealsToChoiceFrom: MutableMap<Int, MealEntity> = mutableMapOf<Int, MealEntity>()
                var sumPopularity = 0
                var countMeals = 0
                var popularity = 0
                var popularityToAdd = 0

                for (meal in meals) {
                    popularity = meal.popularity
                    meal.lastEaten?.let {
                        if (it <= (current.timeInMillis - 86400000 * 4)) {
                            popularityToAdd += 5 * (((current.timeInMillis - it) / 86400000).toInt() - 4)
                        }
                    }
                    if (popularityToAdd < 0) popularityToAdd = 0

                    sumPopularity += popularity
                    mealsToChoiceFrom.put(sumPopularity, meal)

                    countMeals++
                }

                //zufallszahl zwischen 0 und sumPopularity
                if (sumPopularity < 1) {
                    break
                }
                var randomNum = 0
                var fin = false
                var meal: MealEntity? = null
                var mealKey = 0

                while (fin == false && mealsToChoiceFrom.isNotEmpty() && sumPopularity > 0) {
                    randomNum = Random.nextInt(0, sumPopularity)

                    mealKey = findMealWithMap(mealsToChoiceFrom, randomNum)
                    meal = mealsToChoiceFrom[mealKey]

                    if (meal == null) continue

                    if (meal.lastEaten != null && meal.lastEaten!! >= (current.timeInMillis - 86400000 * 6)) {
                        mealsToChoiceFrom.remove(mealKey)
                        sumPopularity -= meal.popularity - 10 * ((current.timeInMillis - meal.lastEaten!!) / 86400000).toInt()

                    } else {
                        fin = true
                    }

                }

                //falls abgebrochen, dann ältesteste meal benutzen
                if (fin == false) {
                    meal = when (weekDay) {
                        Calendar.MONDAY -> planerDao.getOldestMealByMonday()
                        Calendar.TUESDAY -> planerDao.getOldestMealByTuesday()
                        Calendar.WEDNESDAY -> planerDao.getOldestMealByWednesday()
                        Calendar.THURSDAY -> planerDao.getOldestMealByThursday()
                        Calendar.FRIDAY -> planerDao.getOldestMealByFriday()
                        Calendar.SATURDAY -> planerDao.getOldestMealBySaturday()
                        Calendar.SUNDAY -> planerDao.getOldestMealBySunday()
                        else -> null
                    }
                }

                if (meal == null) {
                    break
                }

                planerDao.insert(MealPlanEntity(date = current.timeInMillis, meal = meal.id))
                meal.lastEaten = current.timeInMillis
                planerDao.updateMeal(meal)
                Log.d("NACHRICHT", "Added meal to mealPlan: ${meal.name}")

                current.add(Calendar.DAY_OF_MONTH, 1)
                currentFormated = formatter.format(current.time)
            }

        }
    }

    fun recalcMealPlan() {
        val planerDao: PlanerDao = PlanerDatabase.getDatabase(this).planerDao()
        lifecycleScope.launch {
            // 1. Aktuelles Datum und Zeit als ZonedDateTime erhalten
            val now = ZonedDateTime.now(ZoneId.systemDefault())

            // 2. Einen Tag hinzufügen
            val tomorrow = now.plusDays(1)

            // 3. Zeit auf Tagesbeginn setzen
            val tomorrowStartOfDay = tomorrow
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)

            // 4. ZonedDateTime in Instant umwandeln
            val tomorrowStartOfDayInstant = tomorrowStartOfDay.toInstant()

            // 5. Instant in Unix-Timestamp umwandeln
            val timestamp = tomorrowStartOfDayInstant.toEpochMilli()

            planerDao.deleteMealPlanByMinDate(timestamp)
            calcMealPlan()

            //reload
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController
            findNavController(R.id.nav_host_fragment_content_main).navigate(navController.currentDestination!!.id)
        }

    }

    fun findMealWithMap(meals: MutableMap<Int, MealEntity>, randomNum: Int): Int {
        val keys = meals.keys.sorted()
        if (keys.size == 1) return keys[0]

        for (mealKey in keys) {
            if (randomNum < mealKey) {
                return mealKey
            }
        }
        return 0
    }

    //todo testen, optimieren (reset / update)
    suspend fun updateAllLastEaten() {
        val planerDao: PlanerDao = PlanerDatabase.getDatabase(this).planerDao()

        val mealsPlanLastEaten = planerDao.getLastEaten()
        planerDao.resetLastEaten()

        for (mealPlan in mealsPlanLastEaten) {
            planerDao.updateLastEaten(mealPlan.date, mealPlan.meal)
            Log.d("NACHRICHT", "Last eaten: ${mealPlan.date}")
        }
    }

}