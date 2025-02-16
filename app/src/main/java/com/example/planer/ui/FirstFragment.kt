package com.example.planer.ui

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.planer.databinding.FragmentFirstBinding
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.planer.MainActivity
import com.example.planer.R
import com.example.planer.database.PlanerDao
import com.example.planer.database.PlanerDatabase
import com.example.planer.database.MealEntity
import com.example.planer.database.MealPlanEntity
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hier greifen Sie auf die TextView zu und ändern den Text
//        binding.textviewFirst.text = "Neuer Text für die TextView"
        // Show the loading indicator
        binding.progressBar.visibility = View.VISIBLE
        binding.containerForDynamicRows.visibility = View.GONE

        getPlan()

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
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

            val formatter1 = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val formattedDate1 = formatter1.format(calendar.time)

            textViewLeft.text = formattedDate1.toString()
            textViewLeft.gravity = Gravity.CENTER

            // 3. Erstelle die zweite TextView
            val textViewRight = TextView(requireContext())
            textViewRight.layoutParams = LinearLayout.LayoutParams(
                0, // Breite: 0, um Gewichtung zu verwenden
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f // Gewichtung: 1, um den verfügbaren Platz zu teilen
            )

            val mealId = mealPlanEntity.meal
            val planerDao: PlanerDao = PlanerDatabase.getDatabase(requireContext()).planerDao()

            lifecycleScope.launch {
                val meal: MealEntity? = planerDao.getMealById(mealId)

                if (meal != null) {
                    textViewRight.text = meal.name
                    Log.d("NACHRICHT", "$mealId = ${meal.name}")
                } else {
                    textViewRight.text = "Null"
                    Log.d("NACHRICHT", "NULL")
                }

            }

            textViewRight.gravity = Gravity.CENTER

            // 4. Füge die TextViews zur horizontalen LinearLayout hinzu
            rowLinearLayout.addView(textViewLeft)
            rowLinearLayout.addView(textViewRight)

            // 5. Füge die horizontale LinearLayout zum Container hinzu
            container.addView(rowLinearLayout)
        }


    }

    fun getPlan() {
        val planerDao: PlanerDao = PlanerDatabase.getDatabase(requireContext()).planerDao()

        lifecycleScope.launch {
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