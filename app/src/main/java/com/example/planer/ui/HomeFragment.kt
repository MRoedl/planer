package com.example.planer.ui

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.planer.databinding.FragmentHomeBinding
import androidx.lifecycle.lifecycleScope
import com.example.planer.MainActivity
import com.example.planer.R
import com.example.planer.ViewModel.MealViewModel
import com.example.planer.database.PlanerDao
import com.example.planer.database.PlanerDatabase
import com.example.planer.database.MealEntity
import com.example.planer.database.MealPlanEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.getValue

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {

    // todo: Plan bearbeiten / Gerichte austauschen, verschieben
    // Verlauf von Gerichten
    // Statistiken zu Gerichten (wie oft, Wert / Prozentual)
    // Algo für Plan erstellung anpassen (Wochentage, gewichtung für Wahrscheinlichkeit)

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: MealViewModel by activityViewModels()

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
        binding.containerForDynamicRows.visibility = View.GONE

        getPlan()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun createRows(mealPlan: List<MealPlanEntity>) {
        // Referenz zum Container
        val container = binding.containerForDynamicRows

        // Anzahl der Zeilen, die du erstellen möchtest
        val numberOfLines = 3
        var i = 0

        // Schleife zum Erstellen der Zeilen
        for (mealPlanEntity in mealPlan) {
            // 1. Erstelle die horizontale LinearLayout für eine Zeile
            val rowLinearLayout = LinearLayout(requireContext())
            rowLinearLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            rowLinearLayout.orientation = LinearLayout.HORIZONTAL
            //rowLinearLayout.gravity = Gravity.CENTER_HORIZONTAL

            // 2. Erstelle die erste TextView
            val textViewLeft = TextView(requireContext())
            textViewLeft.layoutParams = LinearLayout.LayoutParams(
                0, // Breite: 0, um Gewichtung zu verwenden
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f // Gewichtung: 1, um den verfügbaren Platz zu teilen
            )
            textViewLeft.setPadding(20, 50, 20, 20)

            // Get the current date as a timestamp
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = mealPlanEntity.date

            val formatter1 = SimpleDateFormat("dd.MM", Locale.getDefault())
            val formattedDate1 = formatter1.format(calendar.time)
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

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

            textViewLeft.text = "$dayOfWeekAsString  ${formattedDate1}"
            textViewLeft.gravity = Gravity.CENTER
            textViewLeft.textSize = 20f

            // 3. Erstelle die zweite TextView
            val textViewRight = TextView(requireContext())
            textViewRight.layoutParams = LinearLayout.LayoutParams(
                0, // Breite: 0, um Gewichtung zu verwenden
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f // Gewichtung: 1, um den verfügbaren Platz zu teilen
            )
            textViewRight.textSize = 20f
            textViewRight.gravity = Gravity.CENTER

            val mealId = mealPlanEntity.meal
            val planerDao: PlanerDao = PlanerDatabase.getDatabase(requireContext()).planerDao()

            // 3.2 Erstelle Button
            var btnEdit = Button(requireContext())
            btnEdit.text = "Edit"
            btnEdit.id = i++

            lifecycleScope.launch {
                val meal: MealEntity? = planerDao.getMealById(mealId)

                if (meal != null) {
                    textViewRight.text = meal.name
                    Log.d("NACHRICHT", "$mealId = ${meal.name}")

                    // 3.2 Erstelle Button
                    btnEdit.tag = mealPlanEntity.id
                    btnEdit.layoutParams = LinearLayout.LayoutParams(
                        0, // Breite: 0, um Gewichtung zu verwenden
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f // Gewichtung: 1, um den verfügbaren Platz zu teilen
                    )

                } else {
                    textViewRight.text = "Null"
                    Log.d("NACHRICHT", "NULL")
                }

            }

            // 4. Füge die TextViews zur horizontalen LinearLayout hinzu
            rowLinearLayout.addView(textViewLeft)
            rowLinearLayout.addView(textViewRight)
            rowLinearLayout.addView(btnEdit)

            // 5. Füge die horizontale LinearLayout zum Container hinzu
            container.addView(rowLinearLayout)

            container.findViewById<Button>(btnEdit.id).setOnClickListener { btn ->
                //viewModel.mealId = btn.tag as Long
                //open list to select meal
                var meals : List<MealEntity> = listOf()
                lifecycleScope.launch {
                    meals = planerDao.getAllMeals()

                    var mealNames : Array<String> = arrayOf()
                    for (meal in meals) {
                        mealNames += meal.name
                    }

                    var selected: Int = 0
                    val builder = AlertDialog.Builder(requireContext())
                    builder
                        .setTitle("Test")
                        .setNegativeButton("Cancel") { dialog, which ->
                            dialog.cancel()
                        }
                        .setPositiveButton("OK") { dialog, which ->
                            //update
                            lifecycleScope.launch {
                                planerDao.updateById(btn.tag as Long, meals[selected].id)
                                Log.d("NACHRICHT", "Updated ${btn.tag} to ${meals[selected].name}")
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

        }


    }

    fun getPlan() {
        val planerDao: PlanerDao = PlanerDatabase.getDatabase(requireContext()).planerDao()

        lifecycleScope.launch {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.insertMealPlan.join()

            val mealPlan: List<MealPlanEntity>? = planerDao.getMealPlan()

            if (mealPlan != null) {
                createRows(mealPlan)

                Log.d("NACHRICHT", "Created rows")
            } else {
                Log.d("NACHRICHT", "NULL")
            }
            binding.progressBar.visibility = View.GONE
            binding.containerForDynamicRows.visibility = View.VISIBLE
        }

    }

}