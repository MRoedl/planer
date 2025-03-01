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

//    private fun replaceFragment(fragment: Fragment){
//        val fragmentManager = supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.frameLayout,fragment)
//        fragmentTransaction.commit()
//    }

    fun calcMealPlan() {
        val planerDao: PlanerDao = PlanerDatabase.getDatabase(this).planerDao()

        insertMealPlan = lifecycleScope.launch {
//            planerDao.deleteAllMeals()
            planerDao.deleteMealPlan()

            val allMeals: List<MealEntity>? = planerDao.getAllMeals()

            if (allMeals != null && allMeals.isNotEmpty()) {
                Log.d("NACHRICHT", "allMeals is NOT NULL")

                var mealPlan: MutableList<MealPlanEntity> = mutableListOf()

                // Get the current date as a timestamp
                val calendar = Calendar.getInstance()
                var currentDateTimestamp = calendar.timeInMillis
                var index = 0

                for (i in 1..7) {
                    if (index > allMeals.size - 1) {
                        index = 0
                    }

                    var meal: MealEntity = allMeals[index]
                    mealPlan.add(MealPlanEntity(date = currentDateTimestamp, meal = meal.id))
                    currentDateTimestamp += 86400000
                    index++
                }

                for (mealPlanEntity in mealPlan) {
                    planerDao.insert(mealPlanEntity)
                }


            } else {
                Log.d("NACHRICHT", "allMeals is NULL")
            }

        }
    }

}