package com.example.planer.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planer.MainActivity
import com.example.planer.Model.DynamicItem
import com.example.planer.Model.MealListAdapter
import com.example.planer.R
import com.example.planer.database.MealEntity
import com.example.planer.database.MealPlanEntity
import com.example.planer.database.PlanerDao
import com.example.planer.database.PlanerDatabase
import com.example.planer.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.collections.mutableListOf

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment(), MealListAdapter.OnMealClickListener {

    // todo: Statistiken zu Gerichten (wie oft, Wert / Prozentual)
    //todo ftp (ftpuser, 61*1NcQG%eat)

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var planerDao: PlanerDao? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Show the loading indicator
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE

        planerDao = PlanerDatabase.getDatabase(requireContext()).planerDao()

        getPlan()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun createRows2(mealPlan: List<MealPlanEntity>){
        // 1. Referenz auf die RecyclerView
        val recyclerView = binding.recyclerView

        // 2. LayoutManager setzen
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 3. Daten erstellen (Beispiel)
        var items = mutableListOf<DynamicItem>()

        var i = 0
        val context = this
        var todayPos = 0

        lifecycleScope.launch {
            // Schleife zum Erstellen der Zeilen
            for (mealPlanEntity in mealPlan) {
                // Get the current date as a timestamp
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = mealPlanEntity.date

                val formatter1 = SimpleDateFormat("dd.MM", Locale.getDefault())
                val formatter2 = SimpleDateFormat("YYYY.MM.dd", Locale.getDefault())
                val formattedDate1 = formatter1.format(calendar.time)
                val currentDateFormated = formatter2.format(calendar.time)
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

                // 1. Erstelle die horizontale LinearLayout für eine Zeile
                val todayFormated = formatter2.format(Calendar.getInstance().timeInMillis)

                var pColor = resources.getColor(R.color.white)
                if (currentDateFormated == todayFormated) {
                    pColor = resources.getColor(R.color.highlighted)
                    todayPos = i
                } else if (currentDateFormated > todayFormated) {
                    pColor = resources.getColor(R.color.future)
                } else {
                    pColor = resources.getColor(R.color.past)
                }

                // 2. Erstelle die erste TextView
                var dayOfWeekAsString = when (dayOfWeek) {
                    Calendar.MONDAY -> "Mo"
                    Calendar.TUESDAY -> "Di"
                    Calendar.WEDNESDAY -> "Mi"
                    Calendar.THURSDAY -> "Do"
                    Calendar.FRIDAY -> "Fr"
                    Calendar.SATURDAY -> "Sa"
                    Calendar.SUNDAY -> "So"
                    else -> "Unbekannt"
                }

                val ptVText = "$dayOfWeekAsString  ${formattedDate1}"

                // 3. Erstelle die zweite TextView
                val mealId = mealPlanEntity.meal

                // 3.2 Erstelle Button
                val pbtnEditId = i++
                var ptvMealText: String = "Null"
                var pbtnEditTag: Long = 0


                val meal: MealEntity? = planerDao?.getMealById(mealId)

                if (meal != null) {
                    ptvMealText = meal.name
                    Log.d("NACHRICHT", "$mealId = ${meal.name}")

                    // 3.2 Erstelle Button
                    pbtnEditTag = mealPlanEntity.id

                } else {
                    ptvMealText = "Null"
                    Log.d("NACHRICHT", "NULL")
                }

                // 4. Füge die TextViews zur horizontalen LinearLayout hinzu
                withContext(Dispatchers.Main) {
                    items.add(DynamicItem.MealPlanItem(pColor = pColor, ptvMealText = ptvMealText, pbtnEditTag = pbtnEditTag, ptVText = ptVText, pbtnEditId = pbtnEditId))
                }
            }

            val adapter = MealListAdapter(items)
            recyclerView.adapter = adapter
            adapter.onMealClickListener = context

            recyclerView.scrollToPosition(todayPos)
        }

        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
    }


    override fun onMealClick(btnTag: Long, unused: Boolean?) {
        var meals: List<MealEntity> = listOf()

        lifecycleScope.launch {
            meals = planerDao?.getAllMeals()!!

            var mealNames: Array<String> = arrayOf()
            for (meal in meals) {
                mealNames += meal.name
            }

            var selected: Int = 0
            val builder = AlertDialog.Builder(requireContext())
            builder
                .setTitle("Gericht auswählen")
                .setNegativeButton("Abbrechen") { dialog, which ->
                    dialog.cancel()
                }
                .setPositiveButton("OK") { dialog, which ->
                    //update
                    lifecycleScope.launch {
                        planerDao?.updateById(btnTag, meals[selected].id)
                        Log.d("NACHRICHT", "Updated $btnTag to ${meals[selected].name}")
                        findNavController().navigate(R.id.action_HomeFragment_to_self) //reload todo: LiveData?
                    }
                }
                .setSingleChoiceItems(
                    mealNames, 0
                ) { dialog, which ->
                    // Do something on item tapped.
                    Log.d("NACHRICHT", "Item tapped: $which - ${meals[which].name}")
                    selected = which
                }

            val dialog = builder.create()
            dialog.show()
        }
    }

    fun getPlan() {

        lifecycleScope.launch {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.insertMealPlan.join()

            val mealPlan: List<MealPlanEntity>? = planerDao?.getMealPlan()

            if (mealPlan != null) {
                createRows2(mealPlan)

                Log.d("NACHRICHT", "Created rows")
            } else {
                Log.d("NACHRICHT", "NULL")
            }

        }

    }

}