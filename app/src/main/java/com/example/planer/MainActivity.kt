package com.example.planer

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.room.Room
import com.example.planer.database.PlanerDao
import com.example.planer.database.PlanerDatabase
import com.example.planer.database.MealEntity
import com.example.planer.database.MealPlanEntity
import com.example.planer.databinding.ActivityMainBinding
import com.example.planer.ui.HomeFragment
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.enums.enumEntries
import kotlin.random.Random
import kotlin.random.nextInt

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    lateinit var insertMealPlan: kotlinx.coroutines.Job

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


        //replaceFragment(HomeFragment())
//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null)
//                .setAnchorView(R.id.fab).show()
//            
//
//        }
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
            R.id.action_settings -> {
                Log.d("NACHRICHT", "Settings clicked")
                val navController2 = findNavController(R.id.nav_host_fragment_content_main)
                navController2.navigate(R.id.MealListFragment)

                true
            }
            R.id.action_refresh -> {
                Log.d("NACHRICHT", "Refresh clicked")
                //calcMealPlan()
                //navController.navigate(navController.currentDestination!!.id)
                findNavController(R.id.nav_host_fragment_content_main).navigate(navController.currentDestination!!.id)
                //todo refresh
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

    fun calcMealPlan() {
        val planerDao: PlanerDao = PlanerDatabase.getDatabase(this).planerDao()

        insertMealPlan = lifecycleScope.launch {
//            planerDao.deleteAllMeals()
            //todo for testing
            planerDao.deleteMealPlan()
            planerDao.resetLastEaten()

            val mealPlan: MealPlanEntity? = planerDao.getLatestMealPlan()

            // Get the current date as a timestamp
            var current = Calendar.getInstance()

            if (mealPlan != null) {
                current.timeInMillis = mealPlan.date
                current.add(Calendar.DAY_OF_MONTH, 1)
            }

            var dayInaWeek = Calendar.getInstance()
            dayInaWeek.add(Calendar.DAY_OF_MONTH, 7)

            //val totalMeals = planerDao.getMealCount()

            val calendar = Calendar.getInstance()
            var weekDay = calendar.get(Calendar.DAY_OF_WEEK)
            var meals: List<MealEntity>? = null

            while (current <= dayInaWeek) {
                //for (i in 1..7) {
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
                    continue
                }

                var mealsToChoiceFrom: MutableMap<Int, MealEntity> = mutableMapOf<Int, MealEntity>()
                var sumPopularity = 0
                var countMeals = 0
                var popularity = 0

                for (meal in meals) {
                    popularity = meal.popularity
                    meal.lastEaten?.let {
                        if (it >= (current.timeInMillis - 86400000 * 4)) {
                            popularity += 10 * ((current.timeInMillis - it) / 86400000).toInt()
                        }
                    }
                    sumPopularity += popularity
                    mealsToChoiceFrom.put(sumPopularity, meal)

                    countMeals++
                }

                //zufallszahl zwischen 0 und sumPopularity
                var randomNum = Random.nextInt(0, sumPopularity)
                var fin = false
                var meal: MealEntity? = null
                var mealKey = 0

                while (fin == false && mealsToChoiceFrom.isNotEmpty() && sumPopularity > 0) {
                    randomNum = Random.nextInt(0, sumPopularity)

                    mealKey = findMealWithMap(mealsToChoiceFrom, randomNum)
                    meal = mealsToChoiceFrom[mealKey]
                    //meal = findMeal(meals, randomNum)

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
                    continue
                }

                planerDao.insert(MealPlanEntity(date = current.timeInMillis, meal = meal.id))
                meal.lastEaten = current.timeInMillis
                planerDao.updateMeal(meal)
                Log.d("NACHRICHT", "Added meal to mealPlan: ${meal.name}")

                current.add(Calendar.DAY_OF_MONTH, 1)
            }



        }
    }

    fun findMeal(meals: List<MealEntity>, randomNum: Int): MealEntity? {
        var randomNum = randomNum
        for (meal in meals) {
            randomNum -= meal.popularity
            if (randomNum <= 0) {
                //meal found
                return meal
            }
        }
        return null
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

}